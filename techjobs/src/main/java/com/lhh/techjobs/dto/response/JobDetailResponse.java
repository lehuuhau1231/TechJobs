package com.lhh.techjobs.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDetailResponse {
    private Integer id;
    private String title;
    private String description;
    private String address;
    private Integer salaryMin;
    private Integer salaryMax;
    private String jobRequire;
    private String benefits;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String companyName;
    private String avatar;
    private String cityName;
    private String jobLevelName;
    private String jobTypeName;
    private String contractTypeName;
    private List<String> jobSkills;

    public JobDetailResponse(Integer id, String title, String description, String address,
                             Integer salaryMin, Integer salaryMax, String jobRequire,
                             String benefits, LocalDate startDate, LocalDate endDate,
                             LocalTime startTime, LocalTime endTime, String companyName,
                             String avatar, String cityName, String jobLevelName,
                             String jobTypeName, String contractTypeName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.address = address;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.jobRequire = jobRequire;
        this.benefits = benefits;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.companyName = companyName;
        this.avatar = avatar;
        this.cityName = cityName;
        this.jobLevelName = jobLevelName;
        this.jobTypeName = jobTypeName;
        this.contractTypeName = contractTypeName;
    }
}
