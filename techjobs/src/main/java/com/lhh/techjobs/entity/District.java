package com.lhh.techjobs.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "district")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class District {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    @OneToOne(mappedBy = "district")
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;
}
