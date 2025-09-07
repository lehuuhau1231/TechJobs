package com.lhh.techjobs.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {
    private Integer id;
    private String title;
    private Integer salaryMin;
    private Integer salaryMax;
    private String address;
    private String companyName;
    private String district;
    private String city;
    private String image;
    private String jobLevelName;
    private List<String> jobSkills;

    public JobResponse(Integer id, String title, Integer salaryMin, Integer salaryMax,
                       String address, String companyName, String district, String city, String image, String jobLevelName) {
        this.id = id;
        this.title = title;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.address = address;
        this.companyName = companyName;
        this.district = district;
        this.city = city;
        this.image = image;
        this.jobLevelName = jobLevelName;
    }
}
