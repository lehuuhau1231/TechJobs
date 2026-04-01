package com.lhh.techjobs.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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

    @Column(name = "major", columnDefinition = "TEXT")
    private String major;

    @Column(name = "experience", columnDefinition = "TEXT")
    private String experience;

    @Column(name = "preferred_location", columnDefinition = "TEXT")
    private String preferredLocation;

    @Column(name = "raw_text", columnDefinition = "TEXT")
    private String rawText;

    @OneToOne(mappedBy = "cvProfile")
    private Candidate candidate;

    public String buildVectorContent() {
        List<String> parts = new ArrayList<>();

        if (title != null && !title.isBlank()) {
            parts.add("Title: " + title);
            parts.add(title); // boost title
        }

        if (skills != null && !skills.isBlank()) {
            parts.add("Skills: " + skills);
            parts.add(skills); // boost skills
        }

        if (education != null && !education.isBlank()) {
            parts.add("Education: " + education);
        }

        if (experience != null && !experience.isBlank()) {
            parts.add("Experience: " + experience);
        }

        if (preferredLocation != null && !preferredLocation.isBlank()) {
            parts.add("Preferred location: " + preferredLocation);
        }

        return String.join(" | ", parts);
    }

    public String buildCareerVectorContent() {
        return """
        Skills: %s |
        Experience: %s |
        Major: %s.
        """.formatted(skills, experience, major);
    }

}
