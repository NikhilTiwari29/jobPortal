package com.jobPortal.service;

import com.jobPortal.dto.ApplicantDTO;
import com.jobPortal.dto.Application;
import com.jobPortal.dto.JobDTO;
import com.jobPortal.entity.Applicant;
import com.jobPortal.entity.Job;
import com.jobPortal.entity.Profile;
import com.jobPortal.enums.ApplicationStatus;
import com.jobPortal.exception.JobPortalException;
import com.jobPortal.repository.JobRepository;
import com.jobPortal.repository.ProfileRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService{

    private final JobRepository jobRepository;

    private final ModelMapper modelMapper;

    public JobServiceImpl(JobRepository jobRepository, ProfileRepository profileRepository, ModelMapper modelMapper) {
        this.jobRepository = jobRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public JobDTO postJob(JobDTO jobDTO) throws JobPortalException {
        jobDTO.setPostTime(LocalDateTime.now());
        Job job = modelMapper.map(jobDTO, Job.class);
        Job savedJob = jobRepository.save(job);
        return modelMapper.map(savedJob, JobDTO.class);
    }

    @Override
    public List<JobDTO> getAllJobs() throws JobPortalException {
        return jobRepository.findAll().stream()
                .map(job -> modelMapper.map(job, JobDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public JobDTO getJob(Long id) throws JobPortalException {
        Job job = jobRepository.findById(id).orElseThrow(() -> new JobPortalException("job.not.found"));
        return modelMapper.map(job, JobDTO.class);
    }

    @Override
    public void applyJob(Long id, ApplicantDTO applicantDTO) throws JobPortalException {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobPortalException("job.not.found"));

        boolean alreadyApplied = job.getApplicants().stream()
                .anyMatch(applicant -> applicant.getEmail().equalsIgnoreCase(applicantDTO.getEmail()));

        if (alreadyApplied) {
            throw new JobPortalException("job.already.applied");
        }

        applicantDTO.setTimestamp(LocalDateTime.now());
        applicantDTO.setApplicationStatus(ApplicationStatus.APPLIED);

        Applicant applicant = modelMapper.map(applicantDTO, Applicant.class);

        applicant.setJob(job);

        job.getApplicants().add(applicant);
        jobRepository.save(job);
    }

    
    @Override
    public List<JobDTO> getHistory(Long id, ApplicationStatus applicationStatus) {
        return List.of();
    }

    @Override
    public List<JobDTO> getJobsPostedBy(Long id) throws JobPortalException {
        return jobRepository.findByPostedBy(id).stream()
                .map(job -> modelMapper.map(job, JobDTO.class))
                .toList();
    }


    @Override
    public void changeAppStatus(Application application) throws JobPortalException {

    }
}
