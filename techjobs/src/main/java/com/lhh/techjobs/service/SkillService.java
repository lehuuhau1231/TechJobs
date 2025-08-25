package com.lhh.techjobs.service;

import com.lhh.techjobs.dto.response.SkillResponse;
import com.lhh.techjobs.repository.SkillRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SkillService {
    SkillRepository skillRepository;

    public List<SkillResponse> getSkills() {
        return skillRepository.findAllSkill();
    }
}
