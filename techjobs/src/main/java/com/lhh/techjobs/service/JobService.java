package com.lhh.techjobs.service;

import com.lhh.techjobs.dto.request.JobCreateRequest;
import com.lhh.techjobs.dto.response.*;
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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
    int PAGE_SIZE = 20;
    EmployerRepository employerRepository;
    CityRepository cityRepository;
    DistrictRepository districtRepository;
    JobLevelRepository jobLevelRepository;
    JobTypeRepository jobTypeRepository;
    ContractTypeRepository contractTypeRepository;
    SkillRepository skillRepository;
    JobMapper jobMapper;
    JobVectorService jobVectorService; // Thêm JobVectorService để đồng bộ lên Redis
    JobAsyncProcessor jobAsyncProcessor;

    private static final List<String> BANNED_KEYWORDS = List.of(
            "lừa đảo", "đa cấp", "việc nhẹ lương cao", "cờ bạc", "cá độ",
            "game bài", "đánh bài", "rửa tiền", "mại dâm", "đồi trụy", "chất cấm", "ma túy"
    );

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

    public List<JobTitleResponse> getTitleJob(Status status) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employer employer = employerRepository.findByUserEmail(email);
        if (employer == null) {
            throw new RuntimeException("Employer not found for email: " + email);
        }
        return this.jobRepository.findAllJobTitles(status, employer);
    }

    public List<JobTitleResponse> getTitleJob() {
        return this.jobRepository.findAllJobTitles(Status.PENDING);
    }

    @Transactional
    public int createJob(JobCreateRequest request) {
        log.info("Bắt đầu tạo job mới với request: {}", request);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Employer employer = employerRepository.findByUserEmail(email);
        if (employer == null) {
            throw new RuntimeException("Không tìm thấy nhà tuyển dụng với email: " + email);
        }
        log.info("Tìm thấy employer với ID: {}", employer.getId());

        Job job = jobMapper.toJob(request);
        job.setEmployer(employer);

        try {
            // Link City
            if (request.getCityId() != null) {
                log.info("Tìm city với ID: {}", request.getCityId());
                City city = cityRepository.findById(request.getCityId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy thành phố với ID: " + request.getCityId()));
                job.setCity(city);
                log.info("Tìm thấy city: {}", city.getName());
            }

            // Link District
            if (request.getDistrictId() != null) {
                log.info("Tìm district với ID: {}", request.getDistrictId());
                District district = districtRepository.findById(request.getDistrictId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy quận/huyện với ID: " + request.getDistrictId()));
                job.setDistrict(district);
                log.info("Tìm thấy district: {}", district.getName());
            }

            // Link JobLevel
            if (request.getJobLevelId() != null) {
                log.info("Tìm jobLevel với ID: {}", request.getJobLevelId());
                JobLevel jobLevel = jobLevelRepository.findById(request.getJobLevelId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy cấp bậc công việc với ID: " + request.getJobLevelId()));
                job.setJobLevel(jobLevel);
                log.info("Tìm thấy jobLevel: {}", jobLevel.getName());
            }

            // Link JobType
            if (request.getJobTypeId() != null) {
                log.info("Tìm jobType với ID: {}", request.getJobTypeId());
                JobType jobType = jobTypeRepository.findById(request.getJobTypeId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy loại công việc với ID: " + request.getJobTypeId()));
                job.setJobType(jobType);
                log.info("Tìm thấy jobType: {}", jobType.getName());
            }

            // Link ContractType
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

            // Link Skills
            if (request.getJobSkillIds() != null && !request.getJobSkillIds().isEmpty()) {
                log.info("Tìm skills với các ID: {}", request.getJobSkillIds());
                List<Skill> skills = skillRepository.findAllById(request.getJobSkillIds());
                log.info("Tìm thấy {} skills", skills.size());
                savedJob.setSkills(skills);
                jobRepository.save(savedJob);
            }


            log.info("Hoàn thành tạo job với ID: {}", savedJob.getId());

            if(TransactionSynchronizationManager.isActualTransactionActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        log.info("Transaction committed, synchronizing job ID {} to Redis", savedJob.getId());
                        jobAsyncProcessor.processModerationAsync(savedJob.getId());
                    }
                });
            }

            return savedJob.getId();
        } catch (Exception e) {
            log.error("Lỗi khi tạo job: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public void updateJob(Integer id, JobCreateRequest request) {
        log.info("Bắt đầu cập nhật công việc ID: {} với request: {}", id, request);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công việc với ID: " + id));

        // Kiểm tra xem nhà tuyển dụng hiện tại có sở hữu công việc này không
        if (!job.getEmployer().getUser().getEmail().equals(email)) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa công việc này.");
        }

        // Cập nhật các trường cơ bản
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setAddress(request.getAddress());
        job.setAgeFrom(request.getAgeFrom());
        job.setAgeTo(request.getAgeTo());
        job.setStartDate(request.getStartDate());
        job.setEndDate(request.getEndDate());
        job.setStartTime(request.getStartTime());
        job.setEndTime(request.getEndTime());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setJobRequire(request.getJobRequire());
        job.setBenefits(request.getBenefits());

        // Reset trạng thái duyệt
        job.setStatus(Status.PENDING);
        job.setRejectReason(null);
        job.setFieldErrors(null);

        // Cập nhật City
        if (request.getCityId() != null) {
            City city = cityRepository.findById(request.getCityId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thành phố với ID: " + request.getCityId()));
            job.setCity(city);
        } else {
            job.setCity(null);
        }

        // Cập nhật District
        if (request.getDistrictId() != null) {
            District district = districtRepository.findById(request.getDistrictId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy quận/huyện với ID: " + request.getDistrictId()));
            job.setDistrict(district);
        } else {
            job.setDistrict(null);
        }

        // Cập nhật JobLevel
        if (request.getJobLevelId() != null) {
            JobLevel jobLevel = jobLevelRepository.findById(request.getJobLevelId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy cấp bậc công việc với ID: " + request.getJobLevelId()));
            job.setJobLevel(jobLevel);
        } else {
            job.setJobLevel(null);
        }

        // Cập nhật JobType
        if (request.getJobTypeId() != null) {
            JobType jobType = jobTypeRepository.findById(request.getJobTypeId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy loại công việc với ID: " + request.getJobTypeId()));
            job.setJobType(jobType);
        } else {
            job.setJobType(null);
        }

        // Cập nhật ContractType
        if (request.getContractTypeId() != null) {
            ContractType contractType = contractTypeRepository.findById(request.getContractTypeId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy loại hợp đồng với ID: " + request.getContractTypeId()));
            job.setContractType(contractType);
        } else {
            job.setContractType(null);
        }

        // Cập nhật Skills
        if (request.getJobSkillIds() != null && !request.getJobSkillIds().isEmpty()) {
            List<Skill> skills = skillRepository.findAllById(request.getJobSkillIds());
            job.setSkills(skills);
        } else {
            job.setSkills(null);
        }

        Job savedJob = jobRepository.save(job);
        log.info("Cập nhật công việc thành công với ID: {}", savedJob.getId());

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    log.info("Transaction committed, starting re-moderation for job ID {}", savedJob.getId());
                    jobAsyncProcessor.processModerationAsync(savedJob.getId());
                }
            });
        }
    }

    public List<JobStatsResponse> getApprovedJobsWithApplicationCount() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employer employer = employerRepository.findByUserEmail(email);
        return jobRepository.findApprovedJobsWithApplicationCount(employer);
    }

    public Page<com.lhh.techjobs.dto.response.JobChatbotResponse> getJobsForChatbot(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return jobRepository.findJobsForChatbot(pageable);
    }

    @Transactional
    public void approveJob(Integer jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công việc với ID: " + jobId));
        job.setStatus(Status.APPROVED);
        jobRepository.save(job);
    }

    @Transactional
    public void rejectJob(Integer jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công việc với ID: " + jobId));
        job.setStatus(Status.CANCELED);
        jobRepository.save(job);
    }

    public List<JobResponse> getPendingJobs() {
        List<JobResponse> jobs = jobRepository.findJobsByStatus(Status.PENDING);
        // Lấy skills cho từng job
        jobs.forEach(job -> {
            job.setJobSkills(jobRepository.findJobSkillsByJobId(job.getId()));
        });
        return jobs;
    }
}
