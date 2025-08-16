package com.lhh.techjobs.repository;

import com.lhh.techjobs.entity.Candidate;
import com.lhh.techjobs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<Candidate, Integer> {
    Candidate findByUserUsername(String username);
}