package com.lhh.techjobs.dto.request;

import com.lhh.techjobs.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PendingStatusApplicationRequest {
    private Integer page;

    @NotNull(message = "Job ID không được để trống")
    private Integer jobId;
}
