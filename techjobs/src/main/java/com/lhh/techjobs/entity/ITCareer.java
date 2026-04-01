package com.lhh.techjobs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "it_careers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ITCareer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "key_skills", columnDefinition = "TEXT")
    private String keySkills;

    @Column(name = "character_traits", columnDefinition = "TEXT")
    private String characterTraits;

    @Column(name = "interests", columnDefinition = "TEXT")
    private String interests;

    @Column(name = "work_styles", columnDefinition = "TEXT")
    private String workStyles;

    @Column(name = "holland_code")
    private String hollandCode;

    @Column(name = "vector_updated_at")
    private LocalDateTime vectorUpdatedAt;
}
