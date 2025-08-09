package com.lhh.techjobs.repository;

import com.lhh.techjobs.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<Candidate, Integer> {
    // Có thể thêm các phương thức custom nếu cần
}