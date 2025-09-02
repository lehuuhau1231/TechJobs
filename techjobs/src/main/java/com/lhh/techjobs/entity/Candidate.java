package com.lhh.techjobs.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "candidate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "full_name")
    private String fullName;
    @Size(max = 300)
    private String selfDescription;
    @Temporal(TemporalType.DATE)
    private LocalDate birthDate;
    @Column(name = "cv")
    private String cv;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "cv_profile_id")
    private CvProfile cvProfile;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<ForeignLanguage> candidateForeignLanguages;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<CandidateSkill> candidateCandidateSkills;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<Application> candidateApplications;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<JobAlert> candidateJobAlerts;

}
