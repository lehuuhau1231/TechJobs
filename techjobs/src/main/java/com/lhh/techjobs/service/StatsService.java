package com.lhh.techjobs.service;

import com.lhh.techjobs.dto.response.RevenueStatsResponse;
import com.lhh.techjobs.enums.BillStatus;
import com.lhh.techjobs.enums.StatsPeriod;
import com.lhh.techjobs.repository.BillRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StatsService {

    BillRepository billRepository;

    /**
     * Thống kê doanh thu theo tháng trong năm
     * @param year Năm cần thống kê
     * @return Danh sách thống kê doanh thu theo từng tháng
     */
    public List<RevenueStatsResponse> getMonthlyRevenueStats(Integer year) {
        if (year == null) {
            year = Year.now().getValue();
        }

        List<Object[]> results = billRepository.getMonthlyRevenueStats(year, BillStatus.PAID);
        List<RevenueStatsResponse> responseList = new ArrayList<>();

        for (Object[] result : results) {
            Integer month = ((Number) result[0]).intValue();
            Long totalBills = ((Number) result[1]).longValue();
            BigDecimal totalRevenue = BigDecimal.valueOf(((Number) result[2]).doubleValue());

            String periodLabel = String.format("Tháng %d/%d", month, year);
            responseList.add(new RevenueStatsResponse(periodLabel, totalBills, totalRevenue));
        }

        return responseList;
    }

    /**
     * Thống kê doanh thu theo quý trong năm
     * @param year Năm cần thống kê
     * @return Danh sách thống kê doanh thu theo từng quý
     */
    public List<RevenueStatsResponse> getQuarterlyRevenueStats(Integer year) {
        if (year == null) {
            year = Year.now().getValue();
        }

        List<Object[]> results = billRepository.getQuarterlyRevenueStats(year, BillStatus.PAID);
        List<RevenueStatsResponse> responseList = new ArrayList<>();

        for (Object[] result : results) {
            Integer quarter = ((Number) result[0]).intValue();
            Long totalBills = ((Number) result[1]).longValue();
            BigDecimal totalRevenue = BigDecimal.valueOf(((Number) result[2]).doubleValue());

            String periodLabel = String.format("Quý %d/%d", quarter, year);
            responseList.add(new RevenueStatsResponse(periodLabel, totalBills, totalRevenue));
        }

        return responseList;
    }

    /**
     * Thống kê doanh thu theo năm
     * @param fromYear Năm bắt đầu thống kê
     * @param toYear Năm kết thúc thống kê
     * @return Danh sách thống kê doanh thu theo từng năm
     */
    public List<RevenueStatsResponse> getYearlyRevenueStats(Integer fromYear, Integer toYear) {
        if (fromYear == null) {
            fromYear = Year.now().getValue() - 5;
        }

        if (toYear == null) {
            toYear = Year.now().getValue();
        }

        List<Object[]> results = billRepository.getYearlyRevenueStats(fromYear, toYear, BillStatus.PAID);
        List<RevenueStatsResponse> responseList = new ArrayList<>();

        for (Object[] result : results) {
            Integer year = ((Number) result[0]).intValue();
            Long totalBills = ((Number) result[1]).longValue();
            BigDecimal totalRevenue = BigDecimal.valueOf(((Number) result[2]).doubleValue());

            String periodLabel = String.format("Năm %d", year);
            responseList.add(new RevenueStatsResponse(periodLabel, totalBills, totalRevenue));
        }

        return responseList;
    }

    /**
     * Thống kê doanh thu theo loại kỳ (tháng/quý/năm)
     * @param periodType Loại kỳ thống kê
     * @param year Năm thống kê
     * @param fromYear Năm bắt đầu (chỉ sử dụng khi periodType là YEAR)
     * @param toYear Năm kết thúc (chỉ sử dụng khi periodType là YEAR)
     * @return Danh sách thống kê doanh thu theo kỳ đã chọn
     */
    public List<RevenueStatsResponse> getRevenueStatsByPeriod(
            StatsPeriod periodType,
            Integer year,
            Integer fromYear,
            Integer toYear) {

        switch (periodType) {
            case MONTH:
                return getMonthlyRevenueStats(year);
            case QUARTER:
                return getQuarterlyRevenueStats(year);
            case YEAR:
                return getYearlyRevenueStats(fromYear, toYear);
            default:
                return new ArrayList<>();
        }
    }
}
