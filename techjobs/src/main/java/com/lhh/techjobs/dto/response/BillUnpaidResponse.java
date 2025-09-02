package com.lhh.techjobs.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillUnpaidResponse {
    private Integer billId;
    private LocalDateTime createdDate;
    private Integer amount;
    private Integer jobId;
    private String jobTitle;
    private String companyName;
}
