package com.lhh.techjobs.repository;

import com.lhh.techjobs.dto.response.CityResponse;
import com.lhh.techjobs.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Integer> {
    List<CityResponse> findAllBy();
}
