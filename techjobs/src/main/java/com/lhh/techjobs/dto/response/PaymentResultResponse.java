package com.lhh.techjobs.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResultResponse {
    private String vnpResponseCode;
    private String vnpOrderInfo;
    private String vnpTransactionNo;
    private String vnpBankCode;
    private String vnpAmount;
    private String vnpTransactionStatus;
    private String message;
    private String status;
}
