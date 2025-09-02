package com.lhh.techjobs.repository;

import com.lhh.techjobs.dto.response.PendingEmployerResponse;
import com.lhh.techjobs.entity.Employer;
import com.lhh.techjobs.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployerRepository extends JpaRepository<Employer, Integer> {
    Employer findByUserEmail(String username);

    @Query("SELECT new com.lhh.techjobs.dto.response.PendingEmployerResponse(e.id, e.companyName, e.taxCode, u.avatar, u.email, u.phone, u.address, u.district, u.city) " +
            "FROM Employer e " +
            "JOIN e.user u " +
            "WHERE e.status = :status")
    List<PendingEmployerResponse> findByStatus(Status status);
}
