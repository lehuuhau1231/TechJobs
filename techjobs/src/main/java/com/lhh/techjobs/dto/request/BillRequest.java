package com.lhh.techjobs.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillRequest {
    @NotNull(message = "Số tiền không được để trống")
    @Min(value = 10000, message = "Số tiền thanh toán tối thiểu là 10,000 VNĐ")
    private Integer amount;

    @NotNull(message = "Job ID không được để trống")
    private Integer jobId;
}
