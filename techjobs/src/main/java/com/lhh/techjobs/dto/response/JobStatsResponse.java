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
public class JobStatsResponse {
    private Integer id;
    private String title;
    private LocalDateTime postedDate;
    private Long applicationCount;
}
