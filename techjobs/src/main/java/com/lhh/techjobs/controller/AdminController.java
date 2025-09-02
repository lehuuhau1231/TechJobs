package com.lhh.techjobs.controller;

import com.lhh.techjobs.dto.response.JobTitleResponse;
import com.lhh.techjobs.dto.response.PendingEmployerResponse;
import com.lhh.techjobs.service.EmployerService;
import com.lhh.techjobs.service.JobService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/admin")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
public class AdminController {
    JobService jobService;
    EmployerService employerService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/job-title")
    public ResponseEntity<List<JobTitleResponse>> getTitleJobPending() {
        return ResponseEntity.ok(jobService.getTitleJob());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/employer/pending")
    public ResponseEntity<List<PendingEmployerResponse>> getAccountEmployerPending() {
        return ResponseEntity.ok(employerService.getPendingEmployers());
    }
}
