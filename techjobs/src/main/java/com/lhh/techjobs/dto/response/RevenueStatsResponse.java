package com.lhh.techjobs.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueStatsResponse {
    private Integer period; // Tháng, quý hoặc năm
    private Long count;    // Số lượng giao dịch
    private Double totalAmount; // Tổng doanh thu
}
