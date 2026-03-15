package com.lhh.techjobs.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobVectorDTO implements Vectorizable{
    private Integer id;
    private String title;
    private String description;
    private Integer salaryMin;
    private Integer salaryMax;
    private String jobLevel;
    private String city;
    private String district;
    private String image;
    private String skills;

    @Override
    public String getId() {
        return id != null ? id.toString() : null;
    }

    // Phương thức này tạo nội dung để vector hóa
    @Override
    public String buildVectorContent() {
        return String.format("""
                Job title: %s | Description: %s | Level: %s | City: %s | District: %s | Skills: %s
                """,
                title, description, jobLevel, city, district, ", ", skills);
    }

    @Override
    public Map<String, String> toMap() {
        return Map.of(
                "id", stringify(id),
                "title", title,
                "city", safeLowerCase(city),
                "salaryMin", stringify(salaryMin),
                "salaryMax", stringify(salaryMax),
                "jobLevelName", jobLevel,
                "districtName", district,
                "skillNames", skills,
                "image", image
        );
    }

    private String stringify(Object value) {
        return value != null ? value.toString() : "";
    }

    private String safeLowerCase(String value) {
        return value != null ? value.toLowerCase() : null;
    }
}
