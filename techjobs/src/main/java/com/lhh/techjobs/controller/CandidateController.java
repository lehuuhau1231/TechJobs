package com.lhh.techjobs.controller;

import com.lhh.techjobs.dto.request.CandidateCreationRequest;
import com.lhh.techjobs.entity.Candidate;
import com.lhh.techjobs.service.CandidateService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/candidate")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CandidateController {
    CandidateService candidateService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createCandidate( 
            @Valid @ModelAttribute CandidateCreationRequest info,
            @RequestParam(value = "avatar") MultipartFile avatar) {
        
        this.candidateService.createCandidate(info, avatar);
    }


}