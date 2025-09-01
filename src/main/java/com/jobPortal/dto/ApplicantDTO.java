package com.jobPortal.dto;

import com.jobPortal.enums.ApplicantStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicantDTO {
    private Long id;
    private LocalDateTime timeStamp;
    private ApplicantStatus applicantStatus;
    private Long jobId;
}

