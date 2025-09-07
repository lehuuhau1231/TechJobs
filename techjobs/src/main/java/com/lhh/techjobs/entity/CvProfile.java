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

    @Column(name = "title", columnDefinition = "TEXT")
    private String title;

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

    public String getVectorContent() {
        StringBuilder sb = new StringBuilder();
        if (title != null) sb.append(title).append(" ");
        if (skills != null) sb.append(skills).append(" ");
        if (education != null) sb.append(education).append(" ");
        if (experience != null) sb.append(experience).append(" ");
        if (preferredLocation != null) sb.append(preferredLocation).append(" ");
        if (preferredSalary != null) sb.append(preferredSalary).append(" ");
        if (rawText != null) sb.append(rawText).append(" ");
        return sb.toString().trim();
    }
}
