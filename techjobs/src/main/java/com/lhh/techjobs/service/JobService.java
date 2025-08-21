package com.lhh.techjobs.service;

import com.lhh.techjobs.dto.response.JobDetailResponse;
import com.lhh.techjobs.dto.response.JobResponse;
import com.lhh.techjobs.dto.response.JobTitleResponse;
import com.lhh.techjobs.entity.Employer;
import com.lhh.techjobs.enums.Status;
import com.lhh.techjobs.repository.EmployerRepository;
import com.lhh.techjobs.repository.JobRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class JobService {
    JobRepository jobRepository;
    int PAGE_SIZE = 5;
    EmployerRepository employerRepository;

    public Page<JobResponse> searchJobs(Map<String, String> params) {
        int page = 0;
        if(params.containsKey("page")) {
            page = Integer.parseInt(params.get("page")) - 1;
        }

        String city = params.getOrDefault("city", null);
        String title = params.getOrDefault("title", null);
        String jobSkill = params.getOrDefault("jobSkill", null);
        String jobLevel = params.getOrDefault("jobLevel", null);
        String jobType = params.getOrDefault("jobType", null);
        String contractType = params.getOrDefault("contractType", null);

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<JobResponse> jobResponses = jobRepository.searchJobs(city, title, jobSkill, jobLevel, jobType, contractType, pageable);

        jobResponses.forEach(jobResponse -> {
            jobResponse.setJobSkills(jobRepository.findJobSkillsByJobId(jobResponse.getId()));
        });

        return jobResponses;
    }

    public JobDetailResponse getJobDetail(Integer jobId) {
        JobDetailResponse jobDetail = jobRepository.findJobDetailById(jobId);
        if (jobDetail == null) {
            throw new RuntimeException("Không tìm thấy công việc với ID: " + jobId);
        }

        List<String> jobSkills = jobRepository.findJobSkillsByJobId(jobId);
        jobDetail.setJobSkills(jobSkills);

        return jobDetail;
    }

    public List<JobTitleResponse> getTitleJob() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Employer employer = employerRepository.findByUserUsername(username);
        if (employer == null) {
            throw new RuntimeException("Employer not found for username: " + username);
        }
        return this.jobRepository.findAllJobTitles(Status.APPROVED, employer);
    }
}
