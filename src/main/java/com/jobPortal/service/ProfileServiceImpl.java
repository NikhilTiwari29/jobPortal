package com.jobPortal.service;

import com.jobPortal.dto.CertificationDTO;
import com.jobPortal.dto.ExperienceDTO;
import com.jobPortal.dto.ProfileDTO;
import com.jobPortal.entity.*;
import com.jobPortal.exception.JobPortalException;
import com.jobPortal.repository.JobRepository;
import com.jobPortal.repository.ProfileRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ModelMapper modelMapper;
    private final JobRepository jobRepository;

    public ProfileServiceImpl(ProfileRepository profileRepository, ModelMapper modelMapper, JobRepository jobRepository) {
        this.profileRepository = profileRepository;
        this.modelMapper = modelMapper;
        this.jobRepository = jobRepository;
    }

    @Transactional
    public void createProfileForUser(User user) {
        log.info("Creating profile for user with id: {}", user.getId());

        Profile profile = new Profile();
        profile.setUser(user);
        profile.setName(user.getUserName());
        profile.setEmail(user.getEmail());
        profile.setJobTitle(null);
        profile.setCompany(null);
        profile.setLocation(null);
        profile.setAbout(null);
        profile.setSkills(new ArrayList<>());
        profile.setExperiences(new ArrayList<>());
        profile.setCertifications(new ArrayList<>());

        profileRepository.save(profile);

        log.debug("Profile created successfully for user with id: {}", user.getId());
    }

    @Override
    public ProfileDTO getProfile(Long id) throws JobPortalException {
        log.info("Fetching profile with id: {}", id);

        Profile profile = profileRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Profile not found for id: {}", id);
                    return new JobPortalException("{user.profile.not.exists}");
                });

        log.debug("Profile fetched successfully for id: {}", id);

        return modelMapper.map(profile, ProfileDTO.class);
    }

    @Override
    public ProfileDTO updateProfile(ProfileDTO profileDTO) throws JobPortalException {
        log.info("Updating profile with id: {}", profileDTO.getId());

        Profile profile = profileRepository.findById(profileDTO.getId()).orElseThrow(
                () -> {
                    log.error("Profile not found for id: {}", profileDTO.getId());
                    return new JobPortalException("{user.profile.not.exists}");
                });

        // --- Scalar fields ---
        profile.setName(profileDTO.getName());
        profile.setEmail(profileDTO.getEmail());
        profile.setJobTitle(profileDTO.getJobTitle());
        profile.setCompany(profileDTO.getCompany());
        profile.setLocation(profileDTO.getLocation());
        profile.setAbout(profileDTO.getAbout());

        // --- Skills ---
        profile.getSkills().clear();
        if (profileDTO.getSkills() != null) {
            profile.getSkills().addAll(profileDTO.getSkills());
        }

        // --- Experiences ---
        profile.getExperiences().clear();
        if (profileDTO.getExperiences() != null) {
            for (ExperienceDTO expDTO : profileDTO.getExperiences()) {
                Experience exp = modelMapper.map(expDTO, Experience.class);
                exp.setProfile(profile); // maintain relationship
                profile.getExperiences().add(exp);
            }
        }

        // --- Certifications ---
        profile.getCertifications().clear();
        if (profileDTO.getCertifications() != null) {
            for (CertificationDTO certDTO : profileDTO.getCertifications()) {
                Certification cert = modelMapper.map(certDTO, Certification.class);
                cert.setProfile(profile); // maintain relationship
                profile.getCertifications().add(cert);
            }
        }


        Profile updatedProfile = profileRepository.save(profile);
        log.debug("Profile updated successfully with id: {}", profileDTO.getId());

        return modelMapper.map(updatedProfile, ProfileDTO.class);
    }

    @Override
    public void saveJob(Long profileId, Long jobId) throws JobPortalException {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new JobPortalException("PROFILE_NOT_FOUND"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobPortalException("JOB_NOT_FOUND"));

        boolean alreadySaved = profile.getSavedJobs().stream()
                .anyMatch(saved -> saved.getId().equals(jobId));

        if (alreadySaved) {
            throw new JobPortalException("JOB_ALREADY_SAVED");
        }

        profile.getSavedJobs().add(job);

        profileRepository.save(profile);
    }
}
