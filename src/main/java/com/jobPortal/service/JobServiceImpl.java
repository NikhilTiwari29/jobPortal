package com.jobPortal.service;

import com.jobPortal.dto.JobDTO;
import com.jobPortal.entity.Job;
import com.jobPortal.repository.JobRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final ModelMapper modelMapper;

    public JobServiceImpl(JobRepository jobRepository, ModelMapper modelMapper) {
        this.jobRepository = jobRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public JobDTO postJob(JobDTO jobDTO) {
        jobDTO.setPostTime(LocalDateTime.now());
        Job job = modelMapper.map(jobDTO, Job.class);
        Job savedJob = jobRepository.save(job);
        return modelMapper.map(savedJob,JobDTO.class);
    }

    @Override
    public List<JobDTO> getAllJobs() {
        return List.of();
    }
}
