package com.lhh.techjobs.dto.response;

import com.lhh.techjobs.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationEmployerResponse {
    private Integer id;
    private LocalDateTime appliedDate;
    private String message;
    private String candidateFullName;
    private String candidateAvatar;
}
