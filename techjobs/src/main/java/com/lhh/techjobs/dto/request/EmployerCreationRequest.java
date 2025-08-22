package com.lhh.techjobs.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EmployerCreationRequest {
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+84|0)\\d{9,10}$", message = "Invalid phone number format")
    private String phone;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Tax code is required")
    private String taxCode;

    private String address;

    private String city;

    private String district;

    private MultipartFile avatar;
}
