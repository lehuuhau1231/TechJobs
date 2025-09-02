package com.lhh.techjobs.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.lhh.techjobs.dto.request.CandidateCreationRequest;
import com.lhh.techjobs.dto.response.CandidateProfileResponse;
import com.lhh.techjobs.entity.Candidate;
import com.lhh.techjobs.entity.User;
import com.lhh.techjobs.exception.AppException;
import com.lhh.techjobs.exception.ErrorCode;
import com.lhh.techjobs.mapper.UserMapper;
import com.lhh.techjobs.repository.CandidateRepository;
import com.lhh.techjobs.repository.SkillRepository;
import com.lhh.techjobs.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CandidateService {
    CandidateRepository candidateRepository;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    Cloudinary cloudinaryClient;
    UserMapper userMapper;
    SkillRepository skillRepository;
    RedisTemplate<String, Object> redisTemplate;
    static String CV_QUEUE = "cvQueue";

    @Transactional
    public void createCandidate(CandidateCreationRequest info) {
        if (userRepository.existsByEmail(info.getEmail()))
            throw new AppException(ErrorCode.USER_EXISTS);
        User user = userMapper.toUserForCandidate(info);
        user.setPassword(passwordEncoder.encode(info.getPassword()));
        MultipartFile avatar = info.getAvatar();
        if (avatar != null && avatar.isEmpty()) {
            try {
                Map uploadResult = cloudinaryClient.uploader().upload(info.getAvatar().getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                user.setAvatar(uploadResult.get("secure_url").toString());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload avatar file", e);
            }
        }
        userRepository.save(user);

        Candidate candidate = new Candidate();
        candidate.setUser(user);
        candidate.setFullName(info.getFullName());
        candidate.setBirthDate(info.getBirthDate());
        candidateRepository.save(candidate);
    }

    public CandidateProfileResponse getCandidateProfileByEmail() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return this.candidateRepository.findCandidateProfileByEmail(email);
    }
}
