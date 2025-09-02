package com.lhh.techjobs.controller;

import com.lhh.techjobs.dto.request.CandidateCreationRequest;
import com.lhh.techjobs.dto.response.CandidateProfileResponse;
import com.lhh.techjobs.service.CVProducer;
import com.lhh.techjobs.service.CandidateService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/candidate")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CandidateController {
    CandidateService candidateService;
    CVProducer cvProducer;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createCandidate( 
            @Valid @ModelAttribute CandidateCreationRequest info) {
        
        this.candidateService.createCandidate(info);
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @PatchMapping("/cv")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Map<String, String>> updateCVCandidate(@RequestParam("cvFile") MultipartFile cvFile) throws IOException {
        String fileUrl = this.cvProducer.queueCVFile(cvFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("cvUrl", fileUrl));
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @GetMapping("/profile")
    public ResponseEntity<CandidateProfileResponse> getCandidateProfile() {
        CandidateProfileResponse profile = candidateService.getCandidateProfileByEmail();
        return ResponseEntity.ok(profile);
    }
}