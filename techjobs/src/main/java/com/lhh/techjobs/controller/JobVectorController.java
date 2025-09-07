package com.lhh.techjobs.controller;

import com.lhh.techjobs.dto.redis.JobVectorDto;
import com.lhh.techjobs.service.JobVectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-vector")
@RequiredArgsConstructor
public class JobVectorController {

    private final JobVectorService jobVectorService;

    /**
     * API đồng bộ tất cả job lên Redis Vector Database
     */
    @PostMapping("/sync-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> syncAllJobsToRedis() {
        try {
            jobVectorService.syncAllJobsToRedis();
            return ResponseEntity.ok("Đã bắt đầu đồng bộ tất cả job lên Redis Vector Database");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi đồng bộ job: " + e.getMessage());
        }
    }

    /**
     * API đồng bộ một job cụ thể lên Redis Vector Database
     */
    @PostMapping("/sync/{jobId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYER')")
    public ResponseEntity<String> syncJobToRedis(@PathVariable Integer jobId) {
        try {
            jobVectorService.syncJobToRedis(jobId);
            return ResponseEntity.ok("Đã đồng bộ job ID " + jobId + " lên Redis Vector Database");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi đồng bộ job: " + e.getMessage());
        }
    }
}
