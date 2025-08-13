package com.lhh.techjobs.repository;

import com.lhh.techjobs.dto.response.ContractTypeResponse;
import com.lhh.techjobs.entity.ContractType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractTypeRepository extends JpaRepository<ContractType, Integer> {
    List<ContractTypeResponse> findAllBy();
}
