package com.lhh.techjobs.repository;

import com.lhh.techjobs.dto.response.BillUnpaidResponse;
import com.lhh.techjobs.dto.response.RevenueStatsResponse;
import com.lhh.techjobs.entity.Bill;
import com.lhh.techjobs.enums.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Integer> {

    @Query("SELECT MONTH(b.createdDate), COUNT(b.id), SUM(b.amount) " +
           "FROM Bill b WHERE YEAR(b.createdDate) = :year AND b.status = :status " +
           "GROUP BY MONTH(b.createdDate) ORDER BY MONTH(b.createdDate)")
    List<Object[]> getMonthlyRevenueStats(@Param("year") Integer year, @Param("status") BillStatus status);

    @Query("SELECT QUARTER(b.createdDate), COUNT(b.id), SUM(b.amount) " +
           "FROM Bill b WHERE YEAR(b.createdDate) = :year AND b.status = :status " +
           "GROUP BY QUARTER(b.createdDate) ORDER BY QUARTER(b.createdDate)")
    List<Object[]> getQuarterlyRevenueStats(@Param("year") Integer year, @Param("status") BillStatus status);

    @Query("SELECT YEAR(b.createdDate), COUNT(b.id), SUM(b.amount) " +
           "FROM Bill b WHERE YEAR(b.createdDate) BETWEEN :fromYear AND :toYear AND b.status = :status " +
           "GROUP BY YEAR(b.createdDate) ORDER BY YEAR(b.createdDate)")
    List<Object[]> getYearlyRevenueStats(
            @Param("fromYear") Integer fromYear,
            @Param("toYear") Integer toYear,
            @Param("status") BillStatus status);

    @Query("SELECT YEAR(b.createdDate) FROM Bill b WHERE b.status = :status GROUP BY YEAR(b.createdDate) ORDER BY YEAR(b.createdDate) DESC" )
    List<Integer> findDistinctYearsWithBills(@Param("status") BillStatus status);

    @Query("SELECT SUM(b.amount), COUNT(b.id) FROM Bill b WHERE b.status = :status")
    Object getTotalAmount(@Param("status") BillStatus status);

    @Query("SELECT new com.lhh.techjobs.dto.response.BillUnpaidResponse(b.id, b.createdDate, b.amount, j.id, j.title, e.companyName) " +
            "FROM Bill b " +
            "JOIN b.job j " +
            "JOIN j.employer e " +
            "WHERE b.status = :status")
    List<BillUnpaidResponse> getBillUnpaid(@Param("status") BillStatus status);
}
