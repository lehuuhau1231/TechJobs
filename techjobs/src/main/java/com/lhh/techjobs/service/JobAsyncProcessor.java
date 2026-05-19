package com.lhh.techjobs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhh.techjobs.dto.response.JobModerationResponse;
import com.lhh.techjobs.entity.Job;
import com.lhh.techjobs.enums.Status;
import com.lhh.techjobs.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobAsyncProcessor {
    private final JobRepository jobRepository;
    private final GeminiService geminiService;
    private final JobVectorService jobVectorService;
    private final ObjectMapper objectMapper;

    @Async
    public void processModerationAsync(int jobId) {
        log.info("Bắt đầu kiểm duyệt bất đồng bộ theo từng trường cho Job ID: {}", jobId);
        Job job = jobRepository.findById(jobId).orElse(null);
        if(job == null) {
            log.error("Không tìm thấy Job ID: {} để kiểm duyệt", jobId);
            return;
        }

        Map<String, String> errors = new HashMap<>();

        // Lớp 1: Kiểm duyệt từ khóa cấm tĩnh cho từng trường cụ thể
        String titleViolation = ContentModerator.checkViolation(job.getTitle());
        if (titleViolation != null) {
            log.warn("Trường Tiêu đề vi phạm từ khóa cấm: {}", titleViolation);
            errors.put("title", "Tiêu đề chứa từ khóa không phù hợp: \"" + titleViolation + "\"");
        }

        String descViolation = ContentModerator.checkViolation(job.getDescription());
        if (descViolation != null) {
            log.warn("Trường Mô tả vi phạm từ khóa cấm: {}", descViolation);
            errors.put("description", "Mô tả công việc chứa từ khóa không phù hợp: \"" + descViolation + "\"");
        }

        String requireViolation = ContentModerator.checkViolation(job.getJobRequire());
        if (requireViolation != null) {
            log.warn("Trường Yêu cầu vi phạm từ khóa cấm: {}", requireViolation);
            errors.put("jobRequire", "Yêu cầu công việc chứa từ khóa không phù hợp: \"" + requireViolation + "\"");
        }

        String benefitsViolation = ContentModerator.checkViolation(job.getBenefits());
        if (benefitsViolation != null) {
            log.warn("Trường Quyền lợi vi phạm từ khóa cấm: {}", benefitsViolation);
            errors.put("benefits", "Quyền lợi chứa từ khóa không phù hợp: \"" + benefitsViolation + "\"");
        }

        // Nếu phát hiện lỗi ở Lớp 1, từ chối và lưu danh sách lỗi dưới dạng JSON
        if (!errors.isEmpty()) {
            try {
                String jsonErrors = objectMapper.writeValueAsString(errors);
                job.setStatus(Status.REJECTED);
                job.setRejectReason("Tin tuyển dụng chứa một số từ khóa không phù hợp với quy chuẩn tuyển dụng.");
                job.setFieldErrors(jsonErrors);
                jobRepository.save(job);
                log.warn("Job ID {} bị từ chối ở Lớp 1 (Từ khóa cấm). Lỗi chi tiết: {}", jobId, jsonErrors);
            } catch (JsonProcessingException e) {
                log.error("Lỗi khi chuyển đổi lỗi kiểm duyệt sang JSON", e);
            }
            return;
        }

        // Lớp 2: Kiểm duyệt chi tiết bằng Gemini AI theo từng trường
        try {
            log.info("Job ID {} vượt qua Lớp 1. Bắt đầu gửi sang Gemini AI kiểm duyệt Lớp 2...", jobId);
            JobModerationResponse jobModerationResponse = geminiService.moderateJobPosting(
                    job.getTitle(), job.getDescription(), job.getJobRequire(), job.getBenefits()
            );

            if (!jobModerationResponse.getIsApproved()) {
                Map<String, String> aiErrors = jobModerationResponse.getFieldErrors();
                if (aiErrors == null) {
                    aiErrors = new HashMap<>();
                }
                
                // Loại bỏ các trường có giá trị null hoặc rỗng
                Map<String, String> filteredErrors = new HashMap<>();
                for (Map.Entry<String, String> entry : aiErrors.entrySet()) {
                    if (entry.getValue() != null && !entry.getValue().isBlank()) {
                        filteredErrors.put(entry.getKey(), entry.getValue());
                    }
                }

                // Nếu AI từ chối nhưng không gán cụ thể cho trường nào, đặt mặc định ở mô tả
                if (filteredErrors.isEmpty()) {
                    filteredErrors.put("description", jobModerationResponse.getRejectReason());
                }

                String jsonErrors = objectMapper.writeValueAsString(filteredErrors);
                job.setStatus(Status.REJECTED);
                job.setRejectReason(jobModerationResponse.getRejectReason());
                job.setFieldErrors(jsonErrors);
                jobRepository.save(job);
                log.warn("Job ID {} bị từ chối bởi Gemini AI ở Lớp 2. Lý do: {}, Lỗi chi tiết: {}", 
                        jobId, jobModerationResponse.getRejectReason(), jsonErrors);
                return;
            }
        } catch (Exception e) {
            log.error("Lỗi khi kiểm duyệt bằng Gemini AI cho Job ID {}: {}. Giữ trạng thái PENDING.", jobId, e.getMessage(), e);
        }

        // Nếu vượt qua cả 2 lớp kiểm duyệt, tiến hành đồng bộ lên Redis Vector Database
        try {
            jobVectorService.syncJobToRedis(job.getId());
            log.info("Kiểm duyệt thành công! Đã đồng bộ Job ID {} lên Redis Vector Database", job.getId());
        } catch (Exception e) {
            log.error("Không thể đồng bộ Job ID {} lên Redis: {}", job.getId(), e.getMessage());
        }
    }
}
