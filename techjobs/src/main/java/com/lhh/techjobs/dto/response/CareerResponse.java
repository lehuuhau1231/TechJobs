package com.lhh.techjobs.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@Data
public class CareerResponse {
    private Integer id;
    private String title;
    private String skillNames;
    private String softSkill;
    private String jobDetailUrl;
    private Double score;

    public String formatCareerDescription() {
        return """
                Title: %s, keySkills: %s, softSkill: %s, jobDetailUrl: %s
                """.formatted(title, skillNames, softSkill, jobDetailUrl);
    }
}
