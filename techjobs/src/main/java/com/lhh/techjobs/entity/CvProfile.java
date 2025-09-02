package com.lhh.techjobs.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "cv_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CvProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills;

    @Column(name = "education", columnDefinition = "TEXT")
    private String education;

    @Column(name = "experience", columnDefinition = "TEXT")
    private String experience;

    @Column(name = "preferred_location", columnDefinition = "TEXT")
    private String preferredLocation;

    @Column(name = "preferred_salary", columnDefinition = "TEXT")
    private String preferredSalary;

    @Column(name = "raw_text", columnDefinition = "TEXT")
    private String rawText;

    @OneToOne(mappedBy = "cvProfile")
    private Candidate candidate;
}
