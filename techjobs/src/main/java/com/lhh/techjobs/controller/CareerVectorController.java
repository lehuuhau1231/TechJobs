package com.lhh.techjobs.controller;

import com.lhh.techjobs.dto.request.CareerRequest;
import com.lhh.techjobs.dto.response.CareerRecommendResponse;
import com.lhh.techjobs.dto.response.JobResponse;
import com.lhh.techjobs.exception.AppException;
import com.lhh.techjobs.exception.ErrorCode;
import com.lhh.techjobs.service.CareerRecommendationService;
import com.lhh.techjobs.service.CareerVectorService;
import com.lhh.techjobs.service.RateLimitService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
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
    RateLimitService rateLimitService;

    @PostMapping("/sync-all")
    @PreAuthorize("hasRole('ADMIN')")
    public String generateCareerVector() {
        careerVectorService.syncAllCareerToRedis();
        return "Synchronization of all careers to Redis Vector Database has been completed.";
    }

    @PostMapping("/search-careers")
    @PreAuthorize("hasRole('CANDIDATE')")
    public CareerRecommendResponse searchCareers(@RequestBody CareerRequest careerRequest, HttpServletRequest httpServletRequest) {
        String clientIp = httpServletRequest.getRemoteAddr();
        Bucket bucket = rateLimitService.resolveBucket(clientIp);
        if(bucket.tryConsume(1)) {
            return careerRecommendationService.recommendCareer(careerRequest);
        } else {
            log.warn("Rate limit exceeded for IP: {}", clientIp);
            throw new AppException(ErrorCode.TOO_MANY_REQUESTS);
        }
    }
}
