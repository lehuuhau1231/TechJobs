package com.lhh.techjobs.service;

import com.lhh.techjobs.dto.request.AuthenticationRequest;
import com.lhh.techjobs.dto.response.AuthenticationResponse;
import com.lhh.techjobs.entity.User;
import com.lhh.techjobs.enums.Role;
import com.lhh.techjobs.enums.Status;
import com.lhh.techjobs.exception.AppException;
import com.lhh.techjobs.exception.ErrorCode;
import com.lhh.techjobs.repository.UserRepository;
import com.nimbusds.jose.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new AppException(ErrorCode.WRONG_USERNAME_OR_PASSWORD);
        }
        if(user.getRole() == Role.EMPLOYER && (user.getEmployer().getStatus() == Status.PENDING || user.getEmployer().getStatus() == Status.CANCELED)) {
            throw new AppException(ErrorCode.EMPLOYER_ACCOUNT_NOT_ACTIVE);
        }
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (authenticated) {
            var token = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(token)
                    .role(user.getRole().toString())
                    .build();
        } else {
            throw new AppException(ErrorCode.WRONG_USERNAME_OR_PASSWORD);
        }
    }
}
