package com.lhh.techjobs.controller;

import com.lhh.techjobs.dto.request.ApplicationRequest;
import com.lhh.techjobs.dto.request.ApplicationStatusRequest;
import com.lhh.techjobs.dto.request.ApplicationFilterRequest;
import com.lhh.techjobs.dto.request.PendingStatusApplicationRequest;
import com.lhh.techjobs.dto.response.ApplicationEmployerResponse;
import com.lhh.techjobs.dto.response.ApplicationFilterResponse;
import com.lhh.techjobs.dto.response.PageResponse;
import com.lhh.techjobs.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/application")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
public class ApplicationController {
    ApplicationService applicationService;

    @PreAuthorize("hasRole('CANDIDATE')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addApplication(@Valid @ModelAttribute ApplicationRequest request) {
        applicationService.addApplication(request);
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @GetMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<ApplicationFilterResponse>> getApplication(@ModelAttribute ApplicationFilterRequest request) {
        return ResponseEntity.ok(applicationService.applicationFilter(request));
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @PostMapping("/status")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateApplicationStatus(@RequestBody @Valid ApplicationStatusRequest request) {
        applicationService.updateApplicationStatus(request);
    }


    @PreAuthorize("hasRole('EMPLOYER')")
    @GetMapping("/pending")
    public ResponseEntity<PageResponse<ApplicationEmployerResponse>> getEmployerApplicationsByJobId(@Valid @ModelAttribute PendingStatusApplicationRequest request) {
        var applications = applicationService.getApplicationsByJobIdForEmployer(request);
        var response = PageResponse.<ApplicationEmployerResponse>builder()
                .content(applications.getContent())
                .page(applications.getNumber())
                .size(applications.getSize())
                .totalElements(applications.getTotalElements())
                .totalPages(applications.getTotalPages())
                .build();
        return ResponseEntity.ok(response);
    }
}
