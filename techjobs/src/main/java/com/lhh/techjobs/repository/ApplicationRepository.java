package com.lhh.techjobs.repository;

import com.lhh.techjobs.dto.response.ApplicationEmployerResponse;
import com.lhh.techjobs.dto.response.ApplicationFilterResponse;
import com.lhh.techjobs.dto.response.InfoMailResponse;
import com.lhh.techjobs.dto.response.CandidateApplicationDetailResponse;
import com.lhh.techjobs.dto.response.CandidateContactResponse;
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

    @Query("SELECT new com.lhh.techjobs.dto.response.ApplicationEmployerResponse(a.id, a.appliedDate, a.message, c.fullName, u.avatar, c.id) " +
            "FROM Application a " +
            "JOIN a.candidate c " +
            "JOIN c.user u " +
            "JOIN a.job j " +
            "WHERE j.status = com.lhh.techjobs.enums.Status.APPROVED AND a.status = com.lhh.techjobs.enums.Status.PENDING " +
            "AND j.id = :jobId AND j.employer = :employer")
    Page<ApplicationEmployerResponse> findApplicationsByJobIdAndEmployer(
            @Param("jobId") Integer jobId,
            @Param("employer") Employer employer,
            Pageable pageable);

    @Query("SELECT new com.lhh.techjobs.dto.response.InfoMailResponse(j.id, u.email) " +
            "FROM Application a " +
            "JOIN a.candidate c " +
            "JOIN c.user u " +
            "JOIN a.job j " +
            "WHERE a.id = :id")
    InfoMailResponse findInfoToSendMail(@Param("id") Integer id);

    @Query("SELECT COUNT(a) > 0 FROM Application a " +
            "JOIN a.job j " +
            "WHERE a.candidate = :candidate AND j.id = :jobId")
    boolean existsByCandidateIdAndJobId(@Param("candidate") Candidate candidate, @Param("jobId") Integer jobId);

    @Query("SELECT new com.lhh.techjobs.dto.response.CandidateApplicationDetailResponse(" +
            "c.id, c.fullName, c.selfDescription, c.birthDate, " +
            "u.email, u.avatar, u.phone, u.address, u.city, u.district, " +
            "a.id, a.appliedDate, a.message, a.cv) " +
            "FROM Application a " +
            "JOIN a.candidate c " +
            "JOIN c.user u " +
            "JOIN a.job j " +
            "JOIN j.employer e " +
            "WHERE c.id = :candidateId AND a.id = :applicationId")
    CandidateApplicationDetailResponse findCandidateApplicationDetail(
            @Param("candidateId") Integer candidateId,
            @Param("applicationId") Integer applicationId);

    @Query("SELECT new com.lhh.techjobs.dto.response.CandidateContactResponse(" +
            "c.id, c.fullName, c.cv, " +
            "u.avatar, u.email, u.phone, u.address, u.city, u.district, " +
            "a.id, a.contacted) " +
            "FROM Application a " +
            "JOIN a.candidate c " +
            "JOIN c.user u " +
            "JOIN a.job j " +
            "WHERE j.id = :jobId AND a.status = :status")
    List<CandidateContactResponse> findApprovedCandidateByApplicationId(
            @Param("jobId") Integer jobId,
            @Param("status") Status status);
}
