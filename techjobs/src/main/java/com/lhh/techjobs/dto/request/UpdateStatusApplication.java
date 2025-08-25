package com.lhh.techjobs.dto.request;

import com.lhh.techjobs.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UpdateStatusApplication {
    @NotNull(message = "Status không được để trống")
    private Status status;

    private Integer page;

    @NotNull(message = "Job ID không được để trống")
    private Integer jobId;
}
