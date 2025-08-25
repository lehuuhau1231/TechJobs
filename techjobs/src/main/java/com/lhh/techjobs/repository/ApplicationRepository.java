package com.lhh.techjobs.repository;

import com.lhh.techjobs.dto.response.ApplicationEmployerResponse;
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

    @Query("SELECT new com.lhh.techjobs.dto.response.ApplicationFilterResponse(a.id, j.id, j.title, a.appliedDate) " +
            "FROM Application a " +
            "JOIN a.job j " +
            "WHERE a.status = :status AND a.candidate = :candidate")
    List<ApplicationFilterResponse> getApplicationByStatus(@Param("status") Status status, @Param("candidate") Candidate candidate);

    @Query("SELECT new com.lhh.techjobs.dto.response.ApplicationEmployerResponse(a.id, a.appliedDate, a.message, c.fullName, u.avatar) " +
            "FROM Application a " +
            "JOIN a.candidate c " +
            "JOIN c.user u " +
            "JOIN a.job j " +
            "WHERE j.status = 'APPROVED' AND a.status = 'PENDING' " +
            "AND j.id = :jobId AND j.employer = :employer")
    Page<ApplicationEmployerResponse> findApplicationsByJobIdAndEmployer(
            @Param("jobId") Integer jobId,
            @Param("employer") Employer employer,
            Pageable pageable);
}
