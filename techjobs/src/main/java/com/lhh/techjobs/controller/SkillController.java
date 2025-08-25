package com.lhh.techjobs.controller;

import com.lhh.techjobs.dto.response.SkillResponse;
import com.lhh.techjobs.service.SkillService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SkillController {
    SkillService skillService;

    @GetMapping
    public ResponseEntity<List<SkillResponse>> getSkills() {
        return new ResponseEntity<>(skillService.getSkills(), HttpStatus.OK);
    }
}
