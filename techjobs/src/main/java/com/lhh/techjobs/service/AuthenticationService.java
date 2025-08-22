package com.lhh.techjobs.service;

import com.lhh.techjobs.dto.request.AuthenticationRequest;
import com.lhh.techjobs.dto.response.AuthenticationResponse;
import com.lhh.techjobs.entity.Candidate;
import com.lhh.techjobs.entity.User;
import com.lhh.techjobs.enums.Role;
import com.lhh.techjobs.enums.Status;
import com.lhh.techjobs.exception.AppException;
import com.lhh.techjobs.exception.ErrorCode;
import com.lhh.techjobs.repository.CandidateRepository;
import com.lhh.techjobs.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CandidateRepository candidateRepository;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new AppException(ErrorCode.WRONG_USERNAME_OR_PASSWORD);
        }

        if(user.getRole() == Role.EMPLOYER && (user.getEmployer().getStatus() == Status.PENDING || user.getEmployer().getStatus() == Status.CANCELED)) {
            throw new AppException(ErrorCode.EMPLOYER_ACCOUNT_NOT_ACTIVE);
        }

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (authenticated) {
            var token = jwtService.generateToken(user);

            AuthenticationResponse.AuthenticationResponseBuilder responseBuilder = AuthenticationResponse.builder()
                    .token(token)
                    .role(user.getRole().toString())
                    .avatar(user.getAvatar() != null ? user.getAvatar() : "https://img.lovepik.com/png/20231104/business-man-avatar-characters-Set-setting_487057_wh1200.png");

            return responseBuilder.build();
        } else {
            throw new AppException(ErrorCode.WRONG_USERNAME_OR_PASSWORD);
        }
    }
}
