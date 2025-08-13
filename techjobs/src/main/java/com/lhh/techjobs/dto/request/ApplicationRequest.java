package com.lhh.techjobs.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationRequest {
    @NotNull(message = "Job ID không được để trống")
    private Integer job;

    private String message;

    @NotNull(message = "CV không được để trống")
    private MultipartFile cv;
}
