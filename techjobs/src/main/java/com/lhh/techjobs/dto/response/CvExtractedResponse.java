package com.lhh.techjobs.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CvExtractedResponse {
    private String name;
    private String email;
    private String phone;
    private List<String> skills;
    private String education;
    private String experience;

    @JsonProperty("preferred_location")
    private String preferredLocation;

    @JsonProperty("preferred_salary")
    private String preferredSalary;
}
