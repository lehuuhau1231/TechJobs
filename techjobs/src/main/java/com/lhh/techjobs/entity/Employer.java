package com.lhh.techjobs.entity;

import com.lhh.techjobs.enums.Role;
import com.lhh.techjobs.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "employer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "company_name")
    private String companyName;
    
    @Column(name = "image")
    private String image;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL)
    private List<Job> jobSkills;

    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL)
    private List<CompanyImage> jobCompanyImages;
}
