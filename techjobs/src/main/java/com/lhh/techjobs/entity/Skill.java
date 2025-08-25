package com.lhh.techjobs.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "skill")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "skills")
    private List<Job> jobs;

    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL)
    private List<CandidateSkill> candidateSkills;
}
