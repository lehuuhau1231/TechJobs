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

    @Scheduled(fixedDelay = 3000)
    public void consumeCVFiles() throws IOException {
        Map<String, Object> payload = (Map<String, Object>) redisTemplate.opsForList().leftPop(CV_QUEUE);
        if (payload != null) {
            String fileName = (String) payload.get("fileName");
            byte[] fileContent = (byte[]) payload.get("content");

            Map<String, Object> parsedresult = parseCV(fileContent);

            System.out.println("Parsed CV: " + fileName);
            System.out.println(parsedresult);
        } else {
            log.info("No CV files to process at the moment.");
        }
    }

    private Map<String, Object> parseCV(byte[] fileBytes) throws IOException {
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(fileBytes))) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            Map<String, Object> data = new HashMap<>();
            String lowerText = text.toLowerCase();

            // ===== Lấy các thông tin cơ bản =====

            data.put("location", extractLocation(text));

            // ===== Lấy các phần nội dung =====
            data.put("skills", extractSection(text, "skills", "experience", "work experience", "education", "projects", "certificates", "career objectives"));
            data.put("experience", extractSection(text, "work experience", "projects", "education", "skills", "certificates", "career objectives"));
            data.put("education", extractSection(text, "education", "work experience", "projects", "skills", "certificates", "career objectives"));
            data.put("projects", extractSection(text, "projects", "work experience", "education", "skills", "certificates", "career objectives"));
            data.put("certificates", extractSection(text, "certificates", "work experience", "education", "projects", "skills", "career objectives"));
            data.put("careerObjectives", extractSection(text, "career objectives", "work experience", "education", "projects", "skills", "certificates"));

            log.info("name: {}", data.get("name"));
            log.info("email: {}", data.get("email"));
            log.info("phone: {}", data.get("phone"));
            log.info("location: {}", data.get("location"));
            log.info("skills: {}", data.get("skills"));
            log.info("experience: {}", data.get("experience"));
            log.info("education: {}", data.get("education"));
            log.info("projects: {}", data.get("projects"));
            log.info("certificates: {}", data.get("certificates"));
            log.info("careerObjectives: {}", data.get("careerObjectives"));

            return data;
        }
    }

        // Hàm tách section từ keyword đến keyword tiếp theo
        private static String extractSection (String text, String startKeyword, String...stopKeywords){
            String lowerText = text.toLowerCase();
            int startIdx = lowerText.indexOf(startKeyword.toLowerCase());
            if (startIdx == -1) return null;

            int endIdx = text.length();
            for (String stopKeyword : stopKeywords) {
                int stopIdx = lowerText.indexOf(stopKeyword.toLowerCase(), startIdx + startKeyword.length());
                if (stopIdx != -1 && stopIdx < endIdx) {
                    endIdx = stopIdx;
                }
            }
            return text.substring(startIdx, endIdx).trim();
        }

        // Lấy location
        private static String extractLocation (String text){
            boolean personalInfoSection = false;
            for (String line : text.split("\n")) {
                if (line.toLowerCase().contains("personal information")) {
                    personalInfoSection = true;
                    continue;
                }
                if (personalInfoSection && line.contains(",") && !line.contains("@") && !line.matches(".*\\d.*")) {
                    return line.trim();
                }
            }
            return null;
        }
    }


