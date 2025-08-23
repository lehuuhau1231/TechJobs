package com.lhh.techjobs.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationFilterResponse {
    private Integer applicationId;
    private Integer jobId;
    private String title;
    private LocalDateTime appliedDate;
}
