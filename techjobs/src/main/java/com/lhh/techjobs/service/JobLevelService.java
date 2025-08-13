package com.lhh.techjobs.service;

import com.lhh.techjobs.dto.response.JobLevelResponse;
import com.lhh.techjobs.entity.JobLevel;
import com.lhh.techjobs.repository.JobLevelRepository;
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
public class JobLevelService {
    JobLevelRepository jobLevelRepository;
    public List<JobLevelResponse> getAllJobLevels() {
        return jobLevelRepository.findAllBy();
    }
}
