package com.lhh.techjobs.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateContactedRequest {
    @NotNull(message = "Application ID không được để trống")
    private Integer applicationId;
}
