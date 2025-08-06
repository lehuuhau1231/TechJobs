package com.lhh.techjobs.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "candidate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Size(max = 300)
    private String selfDescription;
    @Temporal(TemporalType.DATE)
    private Date birthDate;
    @Column(name = "avatar")
    private String avatar;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<ForeignLanguage> candidateForeignLanguages;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<CandidateSkill> candidateCandidateSkills;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<Application> candidateApplications;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<JobAlert> candidateJobAlerts;

}
