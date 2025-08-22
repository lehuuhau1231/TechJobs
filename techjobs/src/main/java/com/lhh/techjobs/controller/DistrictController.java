package com.lhh.techjobs.controller;

import com.lhh.techjobs.dto.response.DistrictResponse;
import com.lhh.techjobs.service.DistrictService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/districts/{cityId}")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DistrictController {
    DistrictService districtService;

    @GetMapping
    public ResponseEntity<List<DistrictResponse>> getDistrictsByCityId(@PathVariable int cityId) {
        List<DistrictResponse> districts = districtService.findDistrictByCityId(cityId);
        return ResponseEntity.ok(districts);
    }
}
