package com.lhh.techjobs.controller;

import com.lhh.techjobs.dto.request.CareerRequest;
import com.lhh.techjobs.dto.response.CareerRecommendResponse;
import com.lhh.techjobs.dto.response.JobResponse;
import com.lhh.techjobs.service.CareerRecommendationService;
import com.lhh.techjobs.service.CareerVectorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/career")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CareerVectorController {
    CareerVectorService careerVectorService;
    CareerRecommendationService careerRecommendationService;

    @PostMapping("/sync-all")
    @PreAuthorize("hasRole('ADMIN')")
    public String generateCareerVector() {
        careerVectorService.syncAllCareerToRedis();
        return "Synchronization of all careers to Redis Vector Database has been completed.";
    }

    @PostMapping("/search-careers")
    @PreAuthorize("hasRole('CANDIDATE')")
    public CareerRecommendResponse searchCareers(@RequestBody CareerRequest careerRequest) {
        return careerRecommendationService.recommendCareer(careerRequest);
    }
}
