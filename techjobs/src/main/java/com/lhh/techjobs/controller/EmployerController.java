package com.lhh.techjobs.controller;

import com.lhh.techjobs.dto.request.CandidateCreationRequest;
import com.lhh.techjobs.dto.request.EmployerCreationRequest;
import com.lhh.techjobs.service.CandidateService;
import com.lhh.techjobs.service.EmployerService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/employer")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmployerController {
    EmployerService employerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createEmployer(
            @Valid @ModelAttribute EmployerCreationRequest info) {

        this.employerService.createEmployer(info);
    }
}
