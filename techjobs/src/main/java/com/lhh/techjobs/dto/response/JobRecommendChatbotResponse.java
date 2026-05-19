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
public class JobRecommendChatbotResponse {
    private Integer id;
    private String url;
    private String title;
    private String address;
    private List<String> skills;
    private String jobLevel;
    private Double score;
}
