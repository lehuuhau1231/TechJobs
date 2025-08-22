package com.lhh.techjobs.service;

import com.lhh.techjobs.dto.response.DistrictResponse;
import com.lhh.techjobs.entity.District;
import com.lhh.techjobs.repository.DistrictRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DistrictService {
    DistrictRepository districtRepository;

    public List<DistrictResponse> findDistrictByCityId(Integer cityId) {
        if (cityId == null || cityId <= 0) {
            throw new IllegalArgumentException("City ID must be a positive integer.");
        }
        return this.districtRepository.findByCityId(cityId);
    }
}
