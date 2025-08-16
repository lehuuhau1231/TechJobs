package com.lhh.techjobs.controller;

import com.lhh.techjobs.dto.request.ApplicationRequest;
import com.lhh.techjobs.dto.request.ApplicationStatusRequest;
import com.lhh.techjobs.dto.response.ApplicationPendingResponse;
import com.lhh.techjobs.dto.response.JobResponse;
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
import java.util.Map;

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

    @PreAuthorize("hasRole('EMPLOYER')")
    @PostMapping("/status")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateApplicationStatus(@RequestBody @Valid ApplicationStatusRequest request) {
        applicationService.updateApplicationStatus(request);
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @GetMapping("/pending")
    public ResponseEntity<PageResponse<ApplicationPendingResponse>> getPendingApplicationsByJobId(@RequestParam Map<String, String> params) {
        var application = applicationService.getPendingApplicationsByJobId(params);
        var response = PageResponse.<ApplicationPendingResponse>builder()
                .content(application.getContent())
                .page(application.getNumber())
                .size(application.getSize())
                .totalElements(application.getTotalElements())
                .totalPages(application.getTotalPages())
                .build();
        return ResponseEntity.ok(response);
    }
}
