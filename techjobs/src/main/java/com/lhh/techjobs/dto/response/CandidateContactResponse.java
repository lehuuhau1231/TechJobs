package com.lhh.techjobs.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateContactResponse {
    private Integer candidateId;
    private String fullName;
    private String cv;
    private String avatar;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String district;
    private Integer applicationId;
    private Boolean contacted;
}
