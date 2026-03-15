package com.lhh.techjobs.controller;

import com.lhh.techjobs.service.CareerVectorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/career")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CareerVectorController {
    CareerVectorService careerVectorService;

    @PostMapping("/sync-all")
    @PreAuthorize("hasRole('ADMIN')")
    public String generateCareerVector() {
        careerVectorService.syncAllCareerToRedis();
        return "Synchronization of all careers to Redis Vector Database has been completed.";
    }
}
