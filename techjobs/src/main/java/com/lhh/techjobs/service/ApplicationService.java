package com.lhh.techjobs.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.lhh.techjobs.dto.request.ApplicationFilterRequest;
import com.lhh.techjobs.dto.request.ApplicationRequest;
import com.lhh.techjobs.dto.request.ApplicationStatusRequest;
import com.lhh.techjobs.dto.response.ApplicationFilterResponse;
import com.lhh.techjobs.dto.response.ApplicationPendingResponse;
import com.lhh.techjobs.entity.Application;
import com.lhh.techjobs.entity.Candidate;
import com.lhh.techjobs.entity.Employer;
import com.lhh.techjobs.entity.Job;
import com.lhh.techjobs.enums.Status;
import com.lhh.techjobs.exception.AppException;
import com.lhh.techjobs.exception.ErrorCode;
import com.lhh.techjobs.repository.ApplicationRepository;
import com.lhh.techjobs.repository.CandidateRepository;
import com.lhh.techjobs.repository.EmployerRepository;
import com.lhh.techjobs.repository.JobRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    EmployerRepository employerRepository;
    int PAGE_SIZE = 10;

    public void addApplication(ApplicationRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Candidate candidate = candidateRepository.findByUserEmail(email);

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

    public List<ApplicationFilterResponse> applicationFilter(ApplicationFilterRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Candidate candidate = candidateRepository.findByUserEmail(email);
        return applicationRepository.getApplicationByStatus(request.getStatus(), candidate);
    }

    public void updateApplicationStatus(ApplicationStatusRequest request) {
        Application application = applicationRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Application not found with id: " + request.getId()));
        application.setStatus(request.getStatus());
        applicationRepository.save(application);
    }

    public Page<ApplicationPendingResponse> getPendingApplicationsByJobId(Map<String, String> params) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employer employer = employerRepository.findByUserEmail(email);

        log.info("Fetching pending applications for employer: {}", employer);

        if (employer == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        if(params.get("jobId") == null || params.get("jobId").isEmpty()) {
            throw new RuntimeException("Job ID is required to fetch pending applications.");
        }

        int page = 0;
        if(params.containsKey("page")) {
            page = Integer.parseInt(params.get("page")) - 1;
        }

        int jobId = Integer.parseInt(params.get("jobId"));
        Pageable pageable = PageRequest.of(page, 10);
        return applicationRepository.findPendingApplicationsByJobIdAndEmployerId(jobId, Status.PENDING, employer, pageable);
    }
}
