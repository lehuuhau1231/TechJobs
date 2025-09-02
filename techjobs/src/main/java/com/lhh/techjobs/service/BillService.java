package com.lhh.techjobs.service;

import com.lhh.techjobs.dto.request.BillRequest;
import com.lhh.techjobs.dto.response.BillResponse;
import com.lhh.techjobs.dto.response.BillUnpaidResponse;
import com.lhh.techjobs.dto.response.RevenueStatsResponse;
import com.lhh.techjobs.entity.Bill;
import com.lhh.techjobs.entity.Job;
import com.lhh.techjobs.enums.BillStatus;
import com.lhh.techjobs.enums.Status;
import com.lhh.techjobs.repository.BillRepository;
import com.lhh.techjobs.repository.JobRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BillService {
    VNPayService VNPayService;
    BillRepository billRepository;
    JobRepository jobRepository;

    public BillResponse createBill(BillRequest request) {
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + request.getJobId()));
        Bill bill = Bill.builder()
                .createdDate(LocalDateTime.now())
                .status(BillStatus.PENDING)
                .amount(request.getAmount())
                .job(job)
                .build();
        billRepository.save(bill);

        return BillResponse.builder()
                .id(bill.getId())
                .amount(bill.getAmount())
                .jobId(job.getId())
                .build();
    }

    public String updateBillStatus(Integer billId, BillStatus billStatus) throws Exception {
        log.info("Update bill status with id {}", billId);
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found with id: " + billId));
        bill.setStatus(billStatus);

        if(billStatus.equals(BillStatus.REFUNDED)) {
            return VNPayService.refundVNPay(String.valueOf(billId), bill.getTxnRef(), bill.getTransactionNo(), bill.getAmount(), bill.getTransactionDate());
        }
        billRepository.save(bill);
        return "success";
    }

    public void updatePaymentBill(Integer billId, String txnRef, String transactionNo, String transactionDate) {
        log.info("Update payment bill with id: " + billId);
        log.info("Update payment bill with txn ref: " + txnRef);
        log.info("Update payment bill with transaction no: " + transactionNo);
        log.info("Update payment bill with transaction date: " + transactionDate);
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found with id: " + billId));
        bill.setStatus(BillStatus.PENDING);
        bill.setTxnRef(txnRef);
        bill.setTransactionNo(transactionNo);
        bill.setTransactionDate(transactionDate);
        billRepository.save(bill);
    }

    public List<Object[]> getMonthlyRevenueStats(Integer year) {
        if (year == null) {
            year = Year.now().getValue();
        }

        return billRepository.getMonthlyRevenueStats(year, BillStatus.PAID);
    }

    public List<Object[]> getQuarterlyRevenueStats(Integer year) {
        if (year == null) {
            year = Year.now().getValue();
        }

        return billRepository.getQuarterlyRevenueStats(year, BillStatus.PAID);
    }

    public List<Object[]> getYearlyRevenueStats(Integer fromYear, Integer toYear) {
        if (fromYear == null) {
            fromYear = Year.now().getValue() - 5;
        }

        if (toYear == null) {
            toYear = Year.now().getValue();
        }

        return billRepository.getYearlyRevenueStats(fromYear, toYear, BillStatus.PAID);
    }

    public List<Integer> getYear() {
        return billRepository.findDistinctYearsWithBills(BillStatus.PAID);
    }

    public Object getTotalAmount() {
        return billRepository.getTotalAmount(BillStatus.PAID);
    }

    public List<BillUnpaidResponse> getBillPending(){
        return billRepository.getBillUnpaid(BillStatus.PENDING);
    }
}
