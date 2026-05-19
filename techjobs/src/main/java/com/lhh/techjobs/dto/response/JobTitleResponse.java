package com.lhh.techjobs.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class JobTitleResponse {
    private Integer id;
    private String title;
    private LocalDateTime createdDate;
    private String rejectReason;
    private String fieldErrors;
}
