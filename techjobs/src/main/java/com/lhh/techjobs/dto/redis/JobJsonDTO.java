package com.lhh.techjobs.dto.redis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class JobJsonDTO implements Vectorizable {
    private Integer id;
    private String url;
    private String title;
    private String address;
    private String description;
    private String requirements;
    private List<String> skills;
    @JsonProperty("job_level")
    private String jobLevel;
    @JsonProperty("soft_skill")
    private List<String> softSkill;

    @Override
    public String getId() {
        return id != null ? id.toString() : null;
    }

    @Override
    public String buildVectorContent() {
        return String.format("""
                Job title: %s | Description: %s | Requirements: %s | Level: %s | Address: %s | Skills: %s | Soft skills: %s
                """,
                title, description, requirements, jobLevel, address, toListFromString(skills), toListFromString(softSkill));
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();

        map.put("id", safeString(id));
        map.put("title", safeString(title));
        map.put("address", safeLower(address));
        map.put("jobLevelName", safeString(jobLevel));
        map.put("requirements", safeString(requirements));
        map.put("skillNames", safeString(skills));
        map.put("softSkill", safeString(softSkill));
        map.put("jobDetailUrl", safeString(url));

        return map;
    }

    @Override
    public Map<String, String> toMapWithUrl(String baseUrl) {
        return Map.of();
    }

    private String toListFromString(List<String> list) {
        return list.isEmpty() ? null : String.join(", ", list);
    }

    private String safeString(Object value) {
        return value == null ? "" : value.toString();
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase();
    }
}
