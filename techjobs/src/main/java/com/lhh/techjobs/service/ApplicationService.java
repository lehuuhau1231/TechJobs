package com.lhh.techjobs.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.lhh.techjobs.dto.request.ApplicationRequest;
import com.lhh.techjobs.entity.Application;
import com.lhh.techjobs.entity.Candidate;
import com.lhh.techjobs.entity.Job;
import com.lhh.techjobs.enums.Status;
import com.lhh.techjobs.repository.ApplicationRepository;
import com.lhh.techjobs.repository.CandidateRepository;
import com.lhh.techjobs.repository.JobRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class ApplicationService {
    ApplicationRepository applicationRepository;
    CandidateRepository candidateRepository;
    Cloudinary cloudinaryClient;
    JobRepository jobRepository;

    public void addApplication(ApplicationRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Candidate candidate = candidateRepository.findByUserUsername(username);

        Job job = jobRepository.findById(request.getJob())
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + request.getJob()));


        try {
            Map uploadResult = cloudinaryClient.uploader().upload(request.getCv().getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));

            Application application = Application.builder()
                    .candidate(candidate)
                    .job(job)
                    .appliedDate(LocalDateTime.now())
                    .message(request.getMessage())
                    .cv(uploadResult.get("secure_url").toString())
                    .status(Status.PENDING)
                    .build();

            applicationRepository.save(application);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload CV file", e);
        }
    }
}
