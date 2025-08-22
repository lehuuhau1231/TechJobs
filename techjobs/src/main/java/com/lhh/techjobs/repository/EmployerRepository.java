package com.lhh.techjobs.repository;

import com.lhh.techjobs.entity.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployerRepository extends JpaRepository<Employer, Integer> {
    Employer findByUserEmail(String username);
}
