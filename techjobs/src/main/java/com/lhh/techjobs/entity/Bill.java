package com.lhh.techjobs.entity;

import com.lhh.techjobs.enums.BillStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bill")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Enumerated(EnumType.STRING)
    private BillStatus status;

    @Column(name = "amount")
    private Integer amount;

    @OneToOne
    @JoinColumn(name = "job_id")
    private Job job;
}
