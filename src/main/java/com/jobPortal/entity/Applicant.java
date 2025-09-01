package com.jobPortal.entity;

import com.jobPortal.enums.ApplicantStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Applicant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timeStamp;

    @Enumerated(EnumType.STRING)
    private ApplicantStatus applicantStatus;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
}
