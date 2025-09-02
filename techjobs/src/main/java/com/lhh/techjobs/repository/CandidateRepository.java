package com.lhh.techjobs.repository;

import com.lhh.techjobs.dto.response.CandidateProfileResponse;
import com.lhh.techjobs.entity.Candidate;
import com.lhh.techjobs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidate, Integer> {
    Candidate findByUserEmail(String userEmail);


    @Query("SELECT new com.lhh.techjobs.dto.response.CandidateProfileResponse(u.avatar, u.email, u.phone, u.address, u.district, u.city, c.id, c.fullName, c.selfDescription, c.cv) " +
            "FROM Candidate c " +
            "JOIN c.user u " +
            "WHERE u.email = :email")
    CandidateProfileResponse findCandidateProfileByEmail(@Param("email") String email);
}