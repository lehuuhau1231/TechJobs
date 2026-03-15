package com.lhh.techjobs.repository;

import com.lhh.techjobs.dto.redis.CareerChatbotDTO;
import com.lhh.techjobs.entity.ITCareer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ITCareerRepository extends JpaRepository<ITCareer, Integer> {
    @Query("""
        SELECT new com.lhh.techjobs.dto.redis.CareerChatbotDTO(
            i.id,
            i.jobTitle,
            i.keySkills,
            i.characterTraits,
            i.hollandCode
        )
        FROM ITCareer i
        WHERE i.vectorUpdatedAt IS NULL
        """)
    Page<CareerChatbotDTO> findTop50ByVectorUpdatedAtIsNull(Pageable pageable);

    @Modifying
    @Query("UPDATE ITCareer i SET i.vectorUpdatedAt = CURRENT_TIMESTAMP WHERE i.id IN :careerIds")
    void updateVectorUpdatedAtForCareers(List<Integer> careerIds);
}
