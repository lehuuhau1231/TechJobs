package com.lhh.techjobs.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_recommendation_ground_truth")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobRecommendGroundTruth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cv_profile_id", nullable = false)
    private CvProfile cvProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(name = "relevance_score")
    private Integer relevanceScore;

}
