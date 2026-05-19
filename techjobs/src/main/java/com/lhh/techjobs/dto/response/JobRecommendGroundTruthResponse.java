package com.lhh.techjobs.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRecommendGroundTruthResponse {
    private Integer cvId;
    private Integer jobId;
    private Integer relevanceScore;
    private String matchReason;
}
