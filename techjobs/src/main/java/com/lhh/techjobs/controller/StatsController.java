package com.lhh.techjobs.controller;

import com.lhh.techjobs.dto.response.RevenueStatsResponse;
import com.lhh.techjobs.enums.StatsPeriod;
import com.lhh.techjobs.service.StatsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatsController {

    StatsService statsService;

    /**
     * API thống kê doanh thu theo tháng/quý/năm
     *
     * @param periodType Loại kỳ thống kê: MONTH (tháng), QUARTER (quý), YEAR (năm)
     * @param year       Năm thống kê (sử dụng cho MONTH và QUARTER)
     * @param fromYear   Năm bắt đầu (chỉ sử dụng cho YEAR)
     * @param toYear     Năm kết thúc (chỉ sử dụng cho YEAR)
     * @return Danh sách thống kê doanh thu
     */
    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RevenueStatsResponse>> getRevenueStats(
            @RequestParam(required = true) StatsPeriod periodType,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer fromYear,
            @RequestParam(required = false) Integer toYear) {

        List<RevenueStatsResponse> result = statsService.getRevenueStatsByPeriod(
                periodType, year, fromYear, toYear);
        return ResponseEntity.ok(result);
    }
}
