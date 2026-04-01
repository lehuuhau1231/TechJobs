package com.lhh.techjobs.dto.response;

import com.lhh.techjobs.enums.Sender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerRecommendResponse {
    private Integer chatSessionId;
    private String text;
    private Sender sender;
}
