package com.lhh.techjobs.service;

import com.lhh.techjobs.dto.redis.JobVectorDto;
import com.lhh.techjobs.entity.Job;
import com.lhh.techjobs.entity.Skill;
import com.lhh.techjobs.enums.Status;
import com.lhh.techjobs.repository.JobRepository;
import com.lhh.techjobs.repository.redis.JobVectorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobVectorService {

    private final JobRepository jobRepository;
    private final JobVectorRepository jobVectorRepository;
    private final JobRedisService jobRedisService;

    /**
     * Đồng bộ tất cả job từ MySQL lên Redis Vector Database
     */
    public void syncAllJobsToRedis() {
        log.info("Bắt đầu đồng bộ tất cả job lên Redis Vector Database");

        int pageSize = 50;
        int pageNumber = 0;
        Page<Job> jobPage;
        int totalSynced = 0;

        do {
            jobPage = jobRepository.findByStatus(Status.APPROVED, PageRequest.of(pageNumber, pageSize));
            List<JobVectorDto> jobVectors = jobPage.getContent().stream()
                    .map(this::convertToJobVectorDto)
                    .collect(Collectors.toList());
            jobVectors.stream().forEach(jobVector -> {
                System.out.println(jobVector);
            });
            jobRedisService.saveAllJob(jobVectors);
            totalSynced += jobVectors.size();
            log.info("Đã đồng bộ {} job, trang {}, tổng cộng: {}", jobVectors.size(), pageNumber + 1, totalSynced);

            pageNumber++;
        } while (jobPage.hasNext());

        log.info("Hoàn thành đồng bộ {} job lên Redis Vector Database", totalSynced);
    }

    /**
     * Đồng bộ một job cụ thể lên Redis Vector Database
     */
    public void syncJobToRedis(Integer jobId) {
        log.info("Đồng bộ job ID {} lên Redis Vector Database", jobId);

        jobRepository.findById(jobId).ifPresent(job -> {
            JobVectorDto jobVectorDto = convertToJobVectorDto(job);
            jobVectorRepository.saveJob(jobVectorDto);
            log.info("Đã đồng bộ job ID {} lên Redis", jobId);
        });
    }

    /**
     * Tìm kiếm job dựa trên vector similarity
     */
    public List<JobVectorDto> searchJobs(String query, int limit) {
        log.info("Tìm kiếm job với query: '{}', limit: {}", query, limit);
        return jobVectorRepository.searchJobsByVector(query, limit);
    }

    /**
     * Xóa job khỏi Redis khi job bị xóa trong MySQL
     */
    public void deleteJobFromRedis(Integer jobId) {
        log.info("Xóa job ID {} khỏi Redis", jobId);
        jobVectorRepository.deleteJob(jobId);
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
    private JobVectorDto convertToJobVectorDto(Job job) {
        return JobVectorDto.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .jobLevelName(job.getJobLevel() != null ? job.getJobLevel().getName() : null)
                .cityName(job.getCity() != null ? job.getCity().getName() : null)
                .districtName(job.getDistrict() != null ? job.getDistrict().getName() : null)
                .skillNames(job.getSkills() != null ?
                        job.getSkills().stream()
                                .map(Skill::getName)
                                .collect(Collectors.toList()) :
                        null)
                .image(job.getEmployer().getUser().getAvatar())
                .build();
    }
}
