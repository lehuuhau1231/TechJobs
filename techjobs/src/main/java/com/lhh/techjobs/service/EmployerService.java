package com.lhh.techjobs.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.lhh.techjobs.dto.request.EmployerCreationRequest;
import com.lhh.techjobs.entity.Candidate;
import com.lhh.techjobs.entity.Employer;
import com.lhh.techjobs.entity.User;
import com.lhh.techjobs.enums.Status;
import com.lhh.techjobs.exception.AppException;
import com.lhh.techjobs.exception.ErrorCode;
import com.lhh.techjobs.mapper.UserMapper;
import com.lhh.techjobs.repository.EmployerRepository;
import com.lhh.techjobs.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
public class EmployerService {
    EmployerRepository employerRepository;
    UserMapper userMapper;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    Cloudinary cloudinaryClient;

    @Transactional
    public void createEmployer(EmployerCreationRequest info) {
        if(userRepository.existsByEmail(info.getEmail()))
            throw new AppException(ErrorCode.USER_EXISTS);

        if (info.getEmail() != null && !info.getEmail().isEmpty() && userRepository.existsByEmail(info.getEmail()))
            throw new AppException(ErrorCode.EMAIL_EXISTS);

        if (info.getPhone() != null && !info.getPhone().isEmpty() && userRepository.existsByPhone(info.getPhone()))
            throw new AppException(ErrorCode.PHONE_EXISTS);

        // Tiếp tục quy trình tạo user
        User user = userMapper.toUserForEmployer(info);
        user.setPassword(passwordEncoder.encode(info.getPassword()));
        MultipartFile avatar = info.getAvatar();
        if (avatar != null && !avatar.isEmpty()) {
            try {
                Map uploadResult = cloudinaryClient.uploader().upload(avatar.getBytes(),
                        ObjectUtils.asMap("folder", "employer_avatars"));
                user.setAvatar(uploadResult.get("secure_url").toString());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload avatar file", e);
            }
        }
        userRepository.save(user);
        // Tạo và lưu employer
        Employer employer = new Employer();
        employer.setUser(user);
        employer.setCompanyName(info.getCompanyName());
        employer.setTaxCode(info.getTaxCode());
        employer.setStatus(Status.PENDING);
        employerRepository.save(employer);
    }
}
