package com.lhh.techjobs.repository;

import com.lhh.techjobs.entity.Bill;
import com.lhh.techjobs.enums.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Integer> {

    // Thống kê doanh thu theo tháng trong năm
    @Query("SELECT FUNCTION('MONTH', b.createdDate) as month, " +
           "COUNT(b) as totalBills, " +
           "SUM(b.amount) as totalRevenue " +
           "FROM Bill b " +
           "WHERE FUNCTION('YEAR', b.createdDate) = :year " +
           "AND b.status = :status " +
           "GROUP BY FUNCTION('MONTH', b.createdDate) " +
           "ORDER BY month")
    List<Object[]> getMonthlyRevenueStats(@Param("year") Integer year,
                                         @Param("status") BillStatus status);

    // Thống kê doanh thu theo quý trong năm
    @Query("SELECT FUNCTION('QUARTER', b.createdDate) as quarter, " +
           "COUNT(b) as totalBills, " +
           "SUM(b.amount) as totalRevenue " +
           "FROM Bill b " +
           "WHERE FUNCTION('YEAR', b.createdDate) = :year " +
           "AND b.status = :status " +
           "GROUP BY FUNCTION('QUARTER', b.createdDate) " +
           "ORDER BY quarter")
    List<Object[]> getQuarterlyRevenueStats(@Param("year") Integer year,
                                           @Param("status") BillStatus status);

    // Thống kê doanh thu theo năm
    @Query("SELECT FUNCTION('YEAR', b.createdDate) as year, " +
           "COUNT(b) as totalBills, " +
           "SUM(b.amount) as totalRevenue " +
           "FROM Bill b " +
           "WHERE FUNCTION('YEAR', b.createdDate) BETWEEN :fromYear AND :toYear " +
           "AND b.status = :status " +
           "GROUP BY FUNCTION('YEAR', b.createdDate) " +
           "ORDER BY year")
    List<Object[]> getYearlyRevenueStats(@Param("fromYear") Integer fromYear,
                                        @Param("toYear") Integer toYear,
                                        @Param("status") BillStatus status);
}
