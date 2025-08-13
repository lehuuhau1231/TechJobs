package com.lhh.techjobs.controller;

import com.lhh.techjobs.dto.request.ApplicationRequest;
import com.lhh.techjobs.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
}
