package com.lhh.techjobs.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "level_language")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LevelLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "name", nullable = false)
    private String name;
} 