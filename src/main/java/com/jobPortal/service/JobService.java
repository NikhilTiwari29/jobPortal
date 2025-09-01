package com.jobPortal.service;

import com.jobPortal.dto.JobDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface JobService {
    JobDTO postJob(JobDTO jobDTO);

    List<JobDTO> getAllJobs();
}
