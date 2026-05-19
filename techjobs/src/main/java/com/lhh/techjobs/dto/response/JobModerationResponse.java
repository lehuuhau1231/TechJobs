package com.lhh.techjobs.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobModerationResponse {
    private Boolean isApproved;
    private String rejectReason;
    private Map<String, String> fieldErrors;
}
