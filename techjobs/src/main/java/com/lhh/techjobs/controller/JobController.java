package com.lhh.techjobs.controller;

import com.lhh.techjobs.dto.request.JobCreateRequest;
import com.lhh.techjobs.dto.response.*;
import com.lhh.techjobs.enums.Status;
import com.lhh.techjobs.service.JobRedisService;
import com.lhh.techjobs.service.JobService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class JobController {

    private final JobService jobService;
    private final JobRedisService jobRedisService;

    @GetMapping
    public ResponseEntity<PageResponse<JobResponse>> searchJobs(@RequestParam Map<String, String> params) {
        var jobPage = jobService.searchJobs(params);
        var response = PageResponse.<JobResponse>builder()
                .content(jobPage.getContent())
                .page(jobPage.getNumber())
                .size(jobPage.getSize())
                .totalElements(jobPage.getTotalElements())
                .totalPages(jobPage.getTotalPages())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobDetailResponse> getJobDetail(@PathVariable Integer id) {
        JobDetailResponse jobDetail = jobService.getJobDetail(id);
        return ResponseEntity.ok(jobDetail);
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @GetMapping("/job-title/{status}")
    public ResponseEntity<List<JobTitleResponse>> getTitleJob(@PathVariable Status status) {
        return ResponseEntity.ok(jobService.getTitleJob(status));
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @PostMapping
    public ResponseEntity<Map<String, Integer>> createJob(@Valid @RequestBody JobCreateRequest request) {
        int jobId = jobService.createJob(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("jobId", jobId));
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Integer>> updateJob(@PathVariable Integer id, @Valid @RequestBody JobCreateRequest request) {
        jobService.updateJob(id, request);
        return ResponseEntity.ok(Map.of("jobId", id));
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @GetMapping("/application-count")
    public ResponseEntity<List<JobStatsResponse>> getJobsWithApplicationCount() {
        List<JobStatsResponse> jobStats = jobService.getApprovedJobsWithApplicationCount();
        return ResponseEntity.ok(jobStats);
    }

    @GetMapping("/chatbot")
    public ResponseEntity<PageResponse<com.lhh.techjobs.dto.response.JobChatbotResponse>> getJobsForChatbot(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var jobPage = jobService.getJobsForChatbot(page, size);
        var response = PageResponse.<com.lhh.techjobs.dto.response.JobChatbotResponse>builder()
                .content(jobPage.getContent())
                .page(jobPage.getNumber())
                .size(jobPage.getSize())
                .totalElements(jobPage.getTotalElements())
                .totalPages(jobPage.getTotalPages())
                .build();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/ingest-jsonl")
    public ResponseEntity<ApiResponse<String>> ingestJobsFromJsonl() {
        try {
            Resource resource = new ClassPathResource("data/jobs_updated.jsonl");
            InputStream inputStream = resource.getInputStream();
            log.info("Starting ingestion from file: {}", resource.getFilename());
            jobRedisService.ingestJobsFromJsonl(inputStream, "jobJson:");
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .message("Successfully ingested jobs from JSONL")
                    .result("Path: " + resource.getInputStream())
                    .build());
        } catch (IOException e) {
            log.error("Failed to read JSONL file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<String>builder()
                            .message("Failed to read JSONL file: " + e.getMessage())
                            .build());
        }
    }
}
