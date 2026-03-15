package com.lhh.techjobs.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@Data
public class CareerResponse {
    private Integer id;
    private String jobTitle;
    private String keySkills;
    private String characterTraits;
    private Integer matchPercentage;
}
