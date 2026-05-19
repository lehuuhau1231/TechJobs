package com.lhh.techjobs.service;

import com.lhh.techjobs.dto.redis.CareerChatbotDTO;
import com.lhh.techjobs.dto.redis.JobVectorDTO;
import com.lhh.techjobs.entity.Job;
import com.lhh.techjobs.entity.Skill;
import com.lhh.techjobs.enums.Status;
import com.lhh.techjobs.mapper.JobMapper;
import com.lhh.techjobs.repository.JobRepository;
import com.lhh.techjobs.repository.projection.JobVectorProjection;
import com.lhh.techjobs.repository.redis.JobVectorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobVectorService {

    private final JobRepository jobRepository;
    private final JobVectorRepository jobVectorRepository;
    private final JobRedisService jobRedisService;
    private final JobMapper jobMapper;

    /**
     * Đồng bộ tất cả job từ MySQL lên Redis Vector Database
     */
    @Transactional
    public void syncAllJobsToRedis() {
        log.info("Starting to synchronize all job to Redis Vector Database...");

        int pageSize = 50;
        List<JobVectorProjection> jobs;
        int totalSynced = 0;
        int batchNumber = 0;
        int offset = 50;

        do {
            jobs = jobRepository.findByStatus(Status.APPROVED.name(), pageSize, offset);
            List<JobVectorDTO> jobVectors = jobs.stream().map(jobMapper::toJobVectorDTO).toList();
            jobRedisService.saveAllJob(jobVectors, "job:");

            List<Integer> jobIds = jobs.stream().map(JobVectorProjection::getId).toList();

            jobRepository.updateVectorUpdatedAtForJobs(jobIds);

            totalSynced += jobVectors.size();
            batchNumber++;
            log.info("Synchronized {} jobs to Redis in {} batch", jobVectors.size(), batchNumber);

            offset += pageSize;
        } while (!jobs.isEmpty());

        log.info("Completed synchronize {} job to vector database Redis", totalSynced);
    }
    /**
     * Đồng bộ một job cụ thể lên Redis Vector Database
     */
    public void syncJobToRedis(Integer jobId) {
        log.info("Synchronizing jobID: {} to Redis", jobId);

        jobRepository.findById(jobId).ifPresent(job -> {
            JobVectorDTO jobVectorDto = convertToJobVectorDto(job);
            jobVectorRepository.saveJob(jobVectorDto);
            log.info("Đã đồng bộ job ID {} lên Redis", jobId);
        });
    }

    /**
     * Lên lịch đồng bộ tất cả job mỗi ngày lúc 2 giờ sáng
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledSyncAllJobs() {
        log.info("Bắt đầu đồng bộ job theo lịch trình tự động");
        syncAllJobsToRedis();
    }

    /**
     * Chuyển đổi entity Job thành JobVectorDto để lưu vào Redis
     */
    private JobVectorDTO convertToJobVectorDto(Job job) {
        return JobVectorDTO.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .jobLevel(job.getJobLevel().getName())
                .city(job.getCity().getName())
                .district(job.getDistrict().getName())
                .skills(job.getSkills().stream().map(Skill::getName).collect(Collectors.joining(", ")))
                .image(job.getEmployer().getUser().getAvatar())
                .build();
    }
}
