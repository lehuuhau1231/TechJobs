package com.lhh.techjobs.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueStatsResponse {
    private String period; // Kỳ thống kê (tháng/quý/năm)
    private Long totalBills; // Tổng số hóa đơn
    private BigDecimal totalRevenue; // Tổng doanh thu
}
