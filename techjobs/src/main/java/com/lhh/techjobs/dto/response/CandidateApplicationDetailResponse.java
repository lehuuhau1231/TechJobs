package com.lhh.techjobs.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateApplicationDetailResponse {
    // Thông tin từ bảng Candidate
    private Integer candidateId;
    private String fullName;
    private String selfDescription;
    private LocalDate birthDate;

    // Thông tin từ bảng User
    private String email;
    private String avatar;
    private String phone;
    private String address;
    private String city;
    private String district;

    // Thông tin từ bảng Application
    private Integer applicationId;
    private LocalDateTime appliedDate;
    private String message;
    private String applicationCv;
}
