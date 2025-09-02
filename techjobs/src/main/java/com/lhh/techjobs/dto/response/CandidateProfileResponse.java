package com.lhh.techjobs.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateProfileResponse {
    // User info
    private String avatar;
    private String email;
    private String phone;
    private String address;
    private String district;
    private String city;

    // Candidate info
    private Integer candidateId;
    private String fullName;
    private String selfDescription;
    private String cv;
}
