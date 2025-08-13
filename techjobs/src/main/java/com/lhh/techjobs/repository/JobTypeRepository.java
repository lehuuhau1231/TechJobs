package com.lhh.techjobs.repository;

import com.lhh.techjobs.dto.response.JobTypeResponse;
import com.lhh.techjobs.entity.JobType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobTypeRepository extends JpaRepository<JobType, Integer> {
    List<JobTypeResponse> findAllBy();
}
