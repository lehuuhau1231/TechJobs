package com.lhh.techjobs.controller;

import com.lhh.techjobs.dto.request.AuthenticationRequest;
import com.lhh.techjobs.dto.response.ApiResponse;
import com.lhh.techjobs.dto.response.AuthenticationResponse;
import com.lhh.techjobs.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }
}
