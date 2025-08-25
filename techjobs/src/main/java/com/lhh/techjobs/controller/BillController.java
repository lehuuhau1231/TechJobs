package com.lhh.techjobs.controller;

import com.lhh.techjobs.dto.request.BillRequest;
import com.lhh.techjobs.dto.request.JobCreateRequest;
import com.lhh.techjobs.dto.response.BillResponse;
import com.lhh.techjobs.service.BillService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/bill")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
public class BillController {
    BillService billService;

    @PostMapping
    public ResponseEntity<BillResponse> createJob(@Valid @RequestBody BillRequest request) {
        return new ResponseEntity<>(this.billService.createBill(request), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateJob(@PathVariable Integer id) {
        this.billService.updateBillStatus(id);
    }
}
