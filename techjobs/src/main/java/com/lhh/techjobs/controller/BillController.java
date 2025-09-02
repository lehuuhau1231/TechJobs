package com.lhh.techjobs.controller;

import com.lhh.techjobs.dto.request.BillRequest;
import com.lhh.techjobs.dto.response.BillResponse;
import com.lhh.techjobs.dto.response.RevenueStatsResponse;
import com.lhh.techjobs.enums.BillStatus;
import com.lhh.techjobs.service.BillService;
import com.lhh.techjobs.service.VNPayService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BillController {
    BillService billService;

    @PreAuthorize("hasRole('EMPLOYER')")
    @PostMapping
    public ResponseEntity<BillResponse> createJob(@Valid @RequestBody BillRequest request) {
        return new ResponseEntity<>(this.billService.createBill(request), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats/monthly")
    public ResponseEntity<List<Object[]>> getMonthlyRevenueStats(
            @RequestParam(required = false) Integer year) {
        List<Object[]> stats = billService.getMonthlyRevenueStats(year);
        return ResponseEntity.ok(stats);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats/quarterly")
    public ResponseEntity<List<Object[]>> getQuarterlyRevenueStats(
            @RequestParam(required = false) Integer year) {
        List<Object[]> stats = billService.getQuarterlyRevenueStats(year);
        return ResponseEntity.ok(stats);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats/yearly")
    public ResponseEntity<List<Object[]>> getYearlyRevenueStats(
            @RequestParam(required = false) Integer fromYear,
            @RequestParam(required = false) Integer toYear) {
        List<Object[]> stats = billService.getYearlyRevenueStats(fromYear, toYear);
        return ResponseEntity.ok(stats);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{billId}/approve")
    public void approveBill(@PathVariable Integer billId) throws Exception {
        billService.updateBillStatus(billId, BillStatus.PAID);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{billId}/reject")
    public void rejectBill(@PathVariable Integer billId) throws Exception {
        String response = billService.updateBillStatus(billId, BillStatus.REFUNDED);
        log.info("response:{}", response);
    }
}
