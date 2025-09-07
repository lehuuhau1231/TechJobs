package com.lhh.techjobs.controller;

import com.lhh.techjobs.dto.response.JobResponse;
import com.lhh.techjobs.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/smart-recommendation")
@RequiredArgsConstructor
public class RecommendationSmartJobController {

    private final RecommendationService recommendationService;

    /**
     * API tìm kiếm job thông minh sử dụng vector similarity với Gemini embedding
     */
    @PreAuthorize("hasRole('CANDIDATE')")
    @GetMapping("/jobs")
    public ResponseEntity<List<JobResponse>> smartSearchJobs() {

        return ResponseEntity.ok(recommendationService.recommendationFromCV());
    }
}
