package com.lhh.techjobs.repository;

import com.lhh.techjobs.dto.response.JobTitleResponse;
import com.lhh.techjobs.entity.Employer;
import com.lhh.techjobs.entity.Job;
import com.lhh.techjobs.dto.response.JobDetailResponse;
import com.lhh.techjobs.dto.response.JobResponse;
import com.lhh.techjobs.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Integer> {
    @Query("SELECT new com.lhh.techjobs.dto.response.JobResponse(j.id, " +
            "j.title, " +
            "j.salaryMin, " +
            "j.salaryMax, " +
            "j.address, " +
            "e.companyName, " +
            "c.name, " +
            "u.avatar) " +
            "FROM Job j " +
            "JOIN j.employer e " +
            "JOIN e.user u " +
            "LEFT JOIN j.city c " +
            "LEFT JOIN j.skills s " +
            "LEFT JOIN j.jobLevel jl " +
            "LEFT JOIN j.jobType jt " +
            "LEFT JOIN j.contractType ct " +
            "WHERE (:city IS NULL OR c.name = :city) " +
            "AND (:title IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "AND (:jobSkill IS NULL OR s.name = :jobSkill) " +
            "AND (:jobLevel IS NULL OR jl.name = :jobLevel) " +
            "AND (:jobType IS NULL OR jt.name = :jobType) " +
            "AND (:contractType IS NULL OR ct.name = :contractType) " +
            "AND (j.status = com.lhh.techjobs.enums.Status.APPROVED) " +
            "GROUP BY j.id, j.title, j.salaryMin, j.salaryMax, j.address, e.companyName, c.name, u.avatar")
    Page<JobResponse> searchJobs(@Param("city") String city,
                               @Param("title") String title,
                               @Param("jobSkill") String jobSkill,
                                 @Param("jobLevel") String jobLevel,
                                 @Param("jobType") String jobType,
                                 @Param("contractType") String contractType,
                               Pageable pageable);

    @Query("SELECT s.name FROM Job j " +
            "JOIN j.skills s " +
            "WHERE j.id = :jobId")
    List<String> findJobSkillsByJobId(@Param("jobId") Integer jobId);

    @Query("SELECT new com.lhh.techjobs.dto.response.JobDetailResponse(j.id, " +
            "j.title, " +
            "j.description, " +
            "c.name, " +
            "d.name, " +
            "j.address, " +
            "j.salaryMin, " +
            "j.salaryMax, " +
            "j.jobRequire, " +
            "j.benefits, " +
            "j.startDate, " +
            "j.endDate, " +
            "j.startTime, " +
            "j.endTime, " +
            "e.companyName, " +
            "u.avatar, " +
            "c.name, " +
            "jl.name, " +
            "jt.name, " +
            "ct.name) " +
            "FROM Job j " +
            "JOIN j.employer e " +
            "JOIN e.user u " +
            "LEFT JOIN j.city c " +
            "LEFT JOIN j.district d " +
            "LEFT JOIN j.jobLevel jl " +
            "LEFT JOIN j.jobType jt " +
            "LEFT JOIN j.contractType ct " +
            "WHERE (j.id = :jobId) " +
            "AND (j.status = com.lhh.techjobs.enums.Status.APPROVED)")
    JobDetailResponse findJobDetailById(@Param("jobId") Integer jobId);

    @Query("SELECT new com.lhh.techjobs.dto.response.JobTitleResponse(j.id, j.title) FROM Job j " +
            "JOIN j.employer e " +
            "WHERE j.status = :status AND j.employer = :employer ")
    List<JobTitleResponse> findAllJobTitles(@Param("status") Status status, @Param("employer") Employer employer);

}
