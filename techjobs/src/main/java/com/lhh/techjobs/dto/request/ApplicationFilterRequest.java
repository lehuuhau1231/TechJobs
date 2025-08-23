package com.lhh.techjobs.dto.request;

import com.lhh.techjobs.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationFilterRequest {
    @NotNull(message = "Phải có status để lọc")
    private Status status;
}
