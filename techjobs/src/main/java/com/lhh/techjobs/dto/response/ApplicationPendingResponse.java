package com.lhh.techjobs.dto.response;

import com.lhh.techjobs.enums.Status;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationPendingResponse {
    private Integer applicationId;
    private LocalDateTime appliedDate;
    private String message;
    private Status status;
    private Integer candidateId;
    private String candidateCv;
}
