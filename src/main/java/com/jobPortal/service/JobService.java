package com.jobPortal.service;

import com.jobPortal.dto.ApplicantDTO;
import com.jobPortal.dto.Application;
import com.jobPortal.dto.JobDTO;
import com.jobPortal.enums.ApplicationStatus;
import com.jobPortal.exception.JobPortalException;

import java.util.List;


public interface JobService {

	 JobDTO postJob(JobDTO jobDTO) throws JobPortalException;

	 List<JobDTO> getAllJobs() throws JobPortalException;

	 JobDTO getJob(Long id) throws JobPortalException;

	 void applyJob(Long id, ApplicantDTO applicantDTO) throws JobPortalException;

	 List<JobDTO> getHistory(Long id, ApplicationStatus applicationStatus);

	 List<JobDTO> getJobsPostedBy(Long id) throws JobPortalException;

	 void changeAppStatus(Application application) throws JobPortalException;
 }
