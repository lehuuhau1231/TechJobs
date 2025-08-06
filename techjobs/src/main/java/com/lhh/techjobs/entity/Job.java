package com.lhh.techjobs.entity;

import com.lhh.techjobs.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "job")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "salary_min")
    private Integer salaryMin;
    
    @Column(name = "salary_max")
    private Integer salaryMax;
    
    @Column(name = "job_require", columnDefinition = "TEXT")
    private String jobRequire;

    @Enumerated(EnumType.STRING)
    private Status status;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(name = "posted_date")
    private LocalDateTime postedDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private Employer employer;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<JobAlert> jobAlerts;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<Application> jobApplications;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<JobSkill> jobSkills;
}
