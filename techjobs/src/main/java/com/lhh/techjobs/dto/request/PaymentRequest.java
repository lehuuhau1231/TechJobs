package com.lhh.techjobs.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    @NotNull(message = "Số tiền không được để trống")
    @Min(value = 10000, message = "Số tiền thanh toán tối thiểu là 10,000 VNĐ")
    private Integer amount;

    @NotNull(message = "Bill ID không được để trống")
    private Integer billId;
}
