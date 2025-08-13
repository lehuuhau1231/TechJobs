package com.lhh.techjobs.entity;

import com.lhh.techjobs.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "application")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    
    @Column(name = "applied_date")
    private LocalDateTime appliedDate;
    
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "cv")
    private String cv;

    @Enumerated(EnumType.STRING)
    private Status status;
}
