package com.jobPortal.controller;

import com.jobPortal.dto.ApplicantDTO;
import com.jobPortal.dto.Application;
import com.jobPortal.dto.JobDTO;
import com.jobPortal.dto.ResponseDTO;
import com.jobPortal.enums.ApplicationStatus;
import com.jobPortal.exception.JobPortalException;
import com.jobPortal.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
@Validated
public class JobController {
	
	private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/post")
	public ResponseEntity<JobDTO>postJob(@RequestBody @Valid JobDTO jobDTO) throws JobPortalException {
		return new ResponseEntity<>(jobService.postJob(jobDTO), HttpStatus.CREATED);
	}
	
	
	@PostMapping("/postAll")
	public ResponseEntity<List<JobDTO>>postAllJob(@RequestBody @Valid List<JobDTO> jobDTOs) throws JobPortalException{
		
		return new ResponseEntity<>(jobDTOs.stream().map((x)->{
			try {
				return jobService.postJob(x);
			} catch (JobPortalException e) {
				e.printStackTrace();
			}
			return x;
		}).toList() , HttpStatus.CREATED);
	}
	
	@GetMapping("/getAll")
	public ResponseEntity<List<JobDTO>>getAllJobs() throws JobPortalException{
		return new ResponseEntity<>(jobService.getAllJobs(), HttpStatus.OK);
	}
	@GetMapping("/{id}")
	public ResponseEntity<JobDTO>getJob(@PathVariable Long id) throws JobPortalException{
		return new ResponseEntity<>(jobService.getJob(id), HttpStatus.OK);
	}
	@PostMapping("apply/{id}")
	public ResponseEntity<ResponseDTO>applyJob(@PathVariable Long id, @RequestBody ApplicantDTO applicantDTO) throws JobPortalException{
		jobService.applyJob(id, applicantDTO);
		return new ResponseEntity<>(new ResponseDTO("Applied Successfully"), HttpStatus.OK);
	}
	@GetMapping("/postedBy/{id}")
	public ResponseEntity<List<JobDTO>>getJobsPostedBy(@PathVariable Long id) throws JobPortalException{
		return new ResponseEntity<>(jobService.getJobsPostedBy(id), HttpStatus.OK);
	}
	
	@GetMapping("/history/{id}/{applicationStatus}")
	public ResponseEntity<List<JobDTO>>getHistory(@PathVariable Long id,@PathVariable ApplicationStatus applicationStatus) throws JobPortalException{
		return new ResponseEntity<>(jobService.getHistory(id, applicationStatus), HttpStatus.OK);
	}

	@PostMapping("/changeAppStatus")
	public ResponseEntity<ResponseDTO>changeAppStatus(@RequestBody Application application) throws JobPortalException{
		jobService.changeAppStatus(application);
		return new ResponseEntity<>(new ResponseDTO("Status Chhanged Successfully"), HttpStatus.OK);
	}




}
