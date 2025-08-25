package com.lhh.techjobs.service;

import com.lhh.techjobs.dto.request.JobCreateRequest;
import com.lhh.techjobs.dto.response.JobDetailResponse;
import com.lhh.techjobs.dto.response.JobResponse;
import com.lhh.techjobs.dto.response.JobTitleResponse;
import com.lhh.techjobs.entity.*;
import com.lhh.techjobs.enums.Status;
import com.lhh.techjobs.mapper.JobMapper;
import com.lhh.techjobs.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class JobService {
    JobRepository jobRepository;
    int PAGE_SIZE = 5;
    EmployerRepository employerRepository;
    CityRepository cityRepository;
    DistrictRepository districtRepository;
    JobLevelRepository jobLevelRepository;
    JobTypeRepository jobTypeRepository;
    ContractTypeRepository contractTypeRepository;
    SkillRepository skillRepository;
    JobMapper jobMapper;

    public Page<JobResponse> searchJobs(Map<String, String> params) {
        int page = 0;
        if(params.containsKey("page")) {
            page = Integer.parseInt(params.get("page")) - 1;
        }

        String city = params.getOrDefault("city", null);
        String title = params.getOrDefault("title", null);
        String jobSkill = params.getOrDefault("jobSkill", null);
        String jobLevel = params.getOrDefault("jobLevel", null);
        String jobType = params.getOrDefault("jobType", null);
        String contractType = params.getOrDefault("contractType", null);

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<JobResponse> jobResponses = jobRepository.searchJobs(city, title, jobSkill, jobLevel, jobType, contractType, pageable);

        jobResponses.forEach(jobResponse -> {
            jobResponse.setJobSkills(jobRepository.findJobSkillsByJobId(jobResponse.getId()));
        });

        return jobResponses;
    }

    public JobDetailResponse getJobDetail(Integer jobId) {
        if(jobId != null && jobId <= 0) {
            throw new IllegalArgumentException("Job ID không hợp lệ");
        }
        JobDetailResponse jobDetail = jobRepository.findJobDetailById(jobId);
        if (jobDetail == null) {
            throw new RuntimeException("Không tìm thấy công việc với ID: " + jobId);
        }

        List<String> jobSkills = jobRepository.findJobSkillsByJobId(jobId);
        jobDetail.setJobSkills(jobSkills);

        return jobDetail;
    }

    public List<JobTitleResponse> getTitleJob() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employer employer = employerRepository.findByUserEmail(email);
        if (employer == null) {
            throw new RuntimeException("Employer not found for email: " + email);
        }
        return this.jobRepository.findAllJobTitles(Status.APPROVED, employer);
    }

    @Transactional
    public int createJob(JobCreateRequest request) {
        log.info("Bắt đầu tạo job mới với request: {}", request);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Email người dùng đang đăng nhập: {}", email);

        Employer employer = employerRepository.findByUserEmail(email);
        if (employer == null) {
            throw new RuntimeException("Không tìm thấy nhà tuyển dụng với email: " + email);
        }
        log.info("Tìm thấy employer với ID: {}", employer.getId());

        Job job = jobMapper.toJob(request);
        job.setEmployer(employer);

        try {
            // Liên kết City
            if (request.getCityId() != null) {
                log.info("Tìm city với ID: {}", request.getCityId());
                City city = cityRepository.findById(request.getCityId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy thành phố với ID: " + request.getCityId()));
                job.setCity(city);
                log.info("Tìm thấy city: {}", city.getName());
            }

            // Liên kết District
            if (request.getDistrictId() != null) {
                log.info("Tìm district với ID: {}", request.getDistrictId());
                District district = districtRepository.findById(request.getDistrictId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy quận/huyện với ID: " + request.getDistrictId()));
                job.setDistrict(district);
                log.info("Tìm thấy district: {}", district.getName());
            }

            // Liên kết JobLevel
            if (request.getJobLevelId() != null) {
                log.info("Tìm jobLevel với ID: {}", request.getJobLevelId());
                JobLevel jobLevel = jobLevelRepository.findById(request.getJobLevelId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy cấp bậc công việc với ID: " + request.getJobLevelId()));
                job.setJobLevel(jobLevel);
                log.info("Tìm thấy jobLevel: {}", jobLevel.getName());
            }

            // Liên kết JobType
            if (request.getJobTypeId() != null) {
                log.info("Tìm jobType với ID: {}", request.getJobTypeId());
                JobType jobType = jobTypeRepository.findById(request.getJobTypeId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy loại công việc với ID: " + request.getJobTypeId()));
                job.setJobType(jobType);
                log.info("Tìm thấy jobType: {}", jobType.getName());
            }

            // Liên kết ContractType
            if (request.getContractTypeId() != null) {
                log.info("Tìm contractType với ID: {}", request.getContractTypeId());
                ContractType contractType = contractTypeRepository.findById(request.getContractTypeId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy loại hợp đồng với ID: " + request.getContractTypeId()));
                job.setContractType(contractType);
                log.info("Tìm thấy contractType: {}", contractType.getName());
            }

            // Lưu job
            log.info("Lưu job vào database");
            job.setStatus(Status.PENDING);
            job.setCreatedDate(LocalDateTime.now());
            Job savedJob = jobRepository.save(job);
            log.info("Job đã được lưu với ID: {}", savedJob.getId());

            // Liên kết Skills
            if (request.getJobSkillIds() != null && !request.getJobSkillIds().isEmpty()) {
                log.info("Tìm skills với các ID: {}", request.getJobSkillIds());
                List<Skill> skills = skillRepository.findAllById(request.getJobSkillIds());
                log.info("Tìm thấy {} skills", skills.size());
                savedJob.setSkills(skills);
                jobRepository.save(savedJob);
            }

            log.info("Hoàn thành tạo job với ID: {}", savedJob.getId());
            return savedJob.getId();
        } catch (Exception e) {
            log.error("Lỗi khi tạo job: {}", e.getMessage(), e);
            throw e;
        }
    }
}
