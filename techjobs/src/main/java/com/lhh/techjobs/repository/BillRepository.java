package com.lhh.techjobs.repository;

import com.lhh.techjobs.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<Bill, Integer> {
}
