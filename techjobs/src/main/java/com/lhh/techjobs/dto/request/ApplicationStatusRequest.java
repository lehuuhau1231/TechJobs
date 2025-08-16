package com.lhh.techjobs.dto.request;

import com.lhh.techjobs.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApplicationStatusRequest {
    @NotNull
    private Integer id;
    @NotNull
    private Status status;
}
