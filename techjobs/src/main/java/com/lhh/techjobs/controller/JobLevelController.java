package com.lhh.techjobs.controller;

import com.lhh.techjobs.dto.response.JobLevelResponse;
import com.lhh.techjobs.entity.JobLevel;
import com.lhh.techjobs.service.JobLevelService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/job-levels")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class JobLevelController {
    JobLevelService jobLevelService;

    @GetMapping
    public ResponseEntity<List<JobLevelResponse>> getAllJobLevels() {
        return ResponseEntity.ok(jobLevelService.getAllJobLevels());
    }
}
