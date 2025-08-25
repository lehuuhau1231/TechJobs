package com.lhh.techjobs.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobCreateRequest {
    @NotBlank(message = "Tiêu đề công việc không được để trống")
    private String title;

    @NotBlank(message = "Mô tả công việc không được để trống")
    private String description;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address;

    @Min(value = 18, message = "Tuổi tối thiểu phải từ 18 trở lên")
    @Max(value = 65, message = "Tuổi tối đa không được vượt quá 65")
    private Integer ageFrom;

    @Min(value = 18, message = "Tuổi tối thiểu phải từ 18 trở lên")
    @Max(value = 65, message = "Tuổi tối đa không được vượt quá 65")
    private Integer ageTo;

    @NotNull(message ="Ngày bắt đầu không được để trống")
    private LocalDate startDate;

    @NotNull(message ="Ngày kết thúc không được để trống")
    private LocalDate endDate;

    @NotNull(message = "Giờ bắt đầu không được để trống")
    private LocalTime startTime;

    @NotNull(message = "Giờ kết thúc không được để trống")
    private LocalTime endTime;

    @Positive(message = "Lương tối thiểu phải là số dương")
    private Integer salaryMin;

    @Positive(message = "Lương tối đa phải là số dương")
    private Integer salaryMax;

    @NotBlank(message = "Yêu cầu công việc không được để trống")
    private String jobRequire;

    @NotBlank(message = "Phúc lợi công việc không được để trống")
    private String benefits;

    @NotNull(message = "Thành phố không được để trống")
    private Integer cityId;

    private Integer districtId;

    @NotNull(message = "Cấp độ công việc không được để trống")
    private Integer jobLevelId;

    @NotNull(message = "Loại công việc không được để trống")
    private Integer jobTypeId;

    @NotNull(message = "Loại hợp đồng không được để trống")
    private Integer contractTypeId;

    private List<Integer> jobSkillIds;
}
