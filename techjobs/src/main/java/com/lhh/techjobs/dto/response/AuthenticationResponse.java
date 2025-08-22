package com.lhh.techjobs.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String token;
    private String role;
    private String avatar;
}
