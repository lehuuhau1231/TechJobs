package com.lhh.techjobs.repository;

import com.lhh.techjobs.entity.Candidate;
import com.lhh.techjobs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidate, Integer> {
    Candidate findByUserEmail(String userEmail);
}