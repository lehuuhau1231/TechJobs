package com.lhh.techjobs.repository;

import com.lhh.techjobs.dto.response.JobLevelResponse;
import com.lhh.techjobs.entity.JobLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobLevelRepository extends JpaRepository<JobLevel, Integer> {
    List<JobLevelResponse> findAllBy();
}
