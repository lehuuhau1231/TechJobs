package com.lhh.techjobs.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_skill")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;
}
