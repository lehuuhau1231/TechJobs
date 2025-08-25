package com.lhh.techjobs.service;

import com.lhh.techjobs.dto.request.BillRequest;
import com.lhh.techjobs.dto.response.BillResponse;
import com.lhh.techjobs.entity.Bill;
import com.lhh.techjobs.entity.Job;
import com.lhh.techjobs.enums.BillStatus;
import com.lhh.techjobs.repository.BillRepository;
import com.lhh.techjobs.repository.JobRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class BillService {
    BillRepository billRepository;
    JobRepository jobRepository;

    public BillResponse createBill(BillRequest request) {
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + request.getJobId()));
        Bill bill = Bill.builder()
                .createdDate(LocalDateTime.now())
                .status(BillStatus.UNPAID)
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

    public void updateBillStatus(Integer billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found with id: " + billId));
        bill.setStatus(BillStatus.PAID);
        billRepository.save(bill);
    }
}
