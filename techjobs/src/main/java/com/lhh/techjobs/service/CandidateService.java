package com.lhh.techjobs.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.lhh.techjobs.dto.request.CandidateCreationRequest;
import com.lhh.techjobs.entity.Candidate;
import com.lhh.techjobs.entity.User;
import com.lhh.techjobs.exception.AppException;
import com.lhh.techjobs.exception.ErrorCode;
import com.lhh.techjobs.mapper.UserMapper;
import com.lhh.techjobs.repository.CandidateRepository;
import com.lhh.techjobs.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

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

    @Transactional
    public void createCandidate(CandidateCreationRequest info, MultipartFile avatar) {
        if (userRepository.existsByUsername(info.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTS);
        User user = userMapper.toUserForCandidate(info);
        user.setPassword(passwordEncoder.encode(info.getPassword()));
        if (!avatar.isEmpty()) {
            try {
                Map uploadResult = cloudinaryClient.uploader().upload(avatar.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                user.setAvatar(uploadResult.get("secure_url").toString());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload avatar file", e);
            }
        }
        userRepository.save(user);

        Candidate candidate = new Candidate();
        candidate.setUser(user);
        candidate.setBirthDate(info.getBirthDate());
        candidateRepository.save(candidate);
    }

    @Transactional
    public void updateCandidateCV(MultipartFile cvFile) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Candidate candidate = candidateRepository.findByUserUsername(username);
        if (candidate != null && cvFile != null && !cvFile.isEmpty()) {
            try {
                Map uploadResult = cloudinaryClient.uploader().upload(cvFile.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                candidate.setCv(uploadResult.get("secure_url").toString());
                candidateRepository.save(candidate);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload CV file", e);
            }
        } else {
            throw new AppException(ErrorCode.FILE_INVALID);
        }
    }
}