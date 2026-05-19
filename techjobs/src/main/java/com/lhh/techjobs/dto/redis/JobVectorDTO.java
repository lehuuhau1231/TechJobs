package com.lhh.techjobs.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
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
    private String softSkill;
    private String jobDetailUrl;

    @Override
    public String getId() {
        return id != null ? id.toString() : null;
    }

    // Phương thức này tạo nội dung để vector hóa
    @Override
    public String buildVectorContent() {
        return String.format("""
                Job title: %s | Description: %s | Level: %s | City: %s | District: %s | Skills: %s | Soft skills: %s
                """,
                title, description, jobLevel, city, district, skills, softSkill);
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();

        map.put("id", safeString(id));
        map.put("title", safeString(title));
        map.put("city", safeLower(city));
        map.put("salaryMin", safeString(salaryMin));
        map.put("salaryMax", safeString(salaryMax));
        map.put("jobLevelName", safeString(jobLevel));
        map.put("districtName", safeString(district));
        map.put("skillNames", safeString(skills));
        map.put("image", safeString(image));
        map.put("softSkill", safeString(softSkill));

        return map;
    }

    @Override
    public Map<String, String> toMapWithUrl(String baseUrl) {
        Map<String, String> map = toMap();

        if (id != null) {
            map.put("jobDetailUrl", baseUrl + id);
        }

        return map;
    }

    private String safeString(Object value) {
        return value == null ? "" : value.toString();
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase();
    }
}
