package com.lhh.techjobs.controller;

import com.lhh.techjobs.dto.response.JobTypeResponse;
import com.lhh.techjobs.service.JobTypeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/job-types")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class JobTypeController {
    JobTypeService jobTypeService;

    @GetMapping
    public ResponseEntity<List<JobTypeResponse>> getAllCities() {
        return ResponseEntity.ok(jobTypeService.getAllJobTypes());
    }
}
