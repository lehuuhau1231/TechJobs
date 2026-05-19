package com.lhh.techjobs.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobChatbotResponse {
    private Integer id;
    private String title;
    private String description;
    private String jobRequire;
    private String benefits;
    private String jobLevelName;
    private String softSkill;
}
