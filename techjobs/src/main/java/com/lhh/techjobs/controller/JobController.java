package com.lhh.techjobs.controller;

import com.lhh.techjobs.dto.request.JobCreateRequest;
import com.lhh.techjobs.dto.response.*;
import com.lhh.techjobs.enums.Status;
import com.lhh.techjobs.service.JobService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class JobController {

    private final JobService jobService;

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
    @GetMapping("/application-count")
    public ResponseEntity<List<JobStatsResponse>> getJobsWithApplicationCount() {
        List<JobStatsResponse> jobStats = jobService.getApprovedJobsWithApplicationCount();
        return ResponseEntity.ok(jobStats);
    }
}
