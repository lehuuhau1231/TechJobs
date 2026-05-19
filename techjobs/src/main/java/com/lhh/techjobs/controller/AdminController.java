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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/admin")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
public class AdminController {
    JobService jobService;
    EmployerService employerService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/jobs/pending")
    public ResponseEntity<List<com.lhh.techjobs.dto.response.JobResponse>> getPendingJobs() {
        return ResponseEntity.ok(jobService.getPendingJobs());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/jobs/{id}/approve")
    public ResponseEntity<Void> approveJob(@PathVariable Integer id) {
        jobService.approveJob(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/jobs/{id}/reject")
    public ResponseEntity<Void> rejectJob(@PathVariable Integer id) {
        jobService.rejectJob(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/employer/pending")
    public ResponseEntity<List<PendingEmployerResponse>> getAccountEmployerPending() {
        return ResponseEntity.ok(employerService.getPendingEmployers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/employer/{id}/approve")
    public ResponseEntity<Void> approveEmployer(@PathVariable Integer id) {
        employerService.approveEmployer(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/employer/{id}/reject")
    public ResponseEntity<Void> rejectEmployer(@PathVariable Integer id) {
        employerService.rejectEmployer(id);
        return ResponseEntity.ok().build();
    }
}
