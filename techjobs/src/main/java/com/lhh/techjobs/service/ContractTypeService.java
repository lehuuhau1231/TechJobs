package com.lhh.techjobs.service;

import com.lhh.techjobs.dto.response.ContractTypeResponse;
import com.lhh.techjobs.repository.ContractTypeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ContractTypeService {
    ContractTypeRepository contractTypeRepository;

    public List<ContractTypeResponse> getAllContractTypes() {
        return contractTypeRepository.findAllBy();
    }
}
