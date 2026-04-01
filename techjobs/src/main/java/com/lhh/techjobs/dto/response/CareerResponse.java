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
    private String interests;
    private String workStyles;
    private String characterTraits;
    private Double score;

    public String formatCareerDescription() {
        return """
                Title: %s, keySkills: %s, interests: %s, workStyles: %s
                """.formatted(jobTitle, keySkills, interests, workStyles);
    }
}
