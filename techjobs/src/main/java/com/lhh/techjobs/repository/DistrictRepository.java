package com.lhh.techjobs.repository;

import com.lhh.techjobs.dto.response.DistrictResponse;
import com.lhh.techjobs.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DistrictRepository extends JpaRepository<District, Integer> {
    @Query("SELECT new com.lhh.techjobs.dto.response.DistrictResponse( d.id, d.name )" +
            "FROM District d " +
            "JOIN d.city c " +
            "WHERE c.id = :id")
    List<DistrictResponse> findByCityId(@Param("id") Integer id);
}
