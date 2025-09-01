package com.jobPortal.controller;

import com.jobPortal.dto.JobDTO;
import com.jobPortal.dto.UserDTO;
import com.jobPortal.exception.JobPortalException;
import com.jobPortal.service.JobService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/jobs")
@Slf4j
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/getAll")
    public ResponseEntity<List<JobDTO>> getAllJobs() throws JobPortalException {
        List<JobDTO> response = jobService.getAllJobs();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
