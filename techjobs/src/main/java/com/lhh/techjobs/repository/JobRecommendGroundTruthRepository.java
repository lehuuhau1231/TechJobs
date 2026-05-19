package com.lhh.techjobs.repository;

import com.lhh.techjobs.entity.JobRecommendGroundTruth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRecommendGroundTruthRepository extends JpaRepository<JobRecommendGroundTruth, Integer> {

    List<JobRecommendGroundTruth> findByCvProfileId(Integer cvProfileId);

    List<JobRecommendGroundTruth> findByCvProfileIdAndRelevanceScoreGreaterThanEqual(
            Integer cvProfileId, Integer minScore);
}
