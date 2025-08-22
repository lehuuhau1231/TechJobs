package com.lhh.techjobs.dto.request;

import java.time.LocalDate;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CandidateCreationRequest {
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+84|0)\\d{9,10}$", message = "Invalid phone number format")
    private String phone;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String address;

    private String city;

    private String district;

    @Past(message = "Birth date must be in the past")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate birthDate;

    private MultipartFile avatar;
} 