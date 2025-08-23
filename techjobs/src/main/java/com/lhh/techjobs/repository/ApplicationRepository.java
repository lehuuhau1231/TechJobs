package com.lhh.techjobs.repository;

import com.lhh.techjobs.dto.response.ApplicationFilterResponse;
import com.lhh.techjobs.dto.response.ApplicationPendingResponse;
import com.lhh.techjobs.entity.Application;
import com.lhh.techjobs.entity.Candidate;
import com.lhh.techjobs.entity.Employer;
import com.lhh.techjobs.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    @Query("SELECT new com.lhh.techjobs.dto.response.ApplicationPendingResponse( a.id, a.appliedDate, a.message, a.status, c.id, c.cv)" +
            " FROM Application a " +
            "JOIN a.candidate c " +
            "JOIN c.user u " +
            "JOIN a.job j " +
            "WHERE j.id = :jobId AND a.status = :status AND j.employer = :employer")
    Page<ApplicationPendingResponse> findPendingApplicationsByJobIdAndEmployerId(
            @Param("jobId") Integer jobId,
            @Param("status") Status status,
            @Param("employer") Employer employer,
            Pageable pageable);

    @Query("SELECT new com.lhh.techjobs.dto.response.ApplicationFilterResponse(a.id, j.id, j.title, a.appliedDate) " +
            "FROM Application a " +
            "JOIN a.job j " +
            "WHERE a.status = :status AND a.candidate = :candidate")
    List<ApplicationFilterResponse> getApplicationByStatus(@Param("status") Status status, @Param("candidate") Candidate candidate);
}
