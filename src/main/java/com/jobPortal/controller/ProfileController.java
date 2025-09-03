package com.jobPortal.controller;

import com.jobPortal.dto.ProfileDTO;
import com.jobPortal.exception.JobPortalException;
import com.jobPortal.service.ProfileService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileDTO> getProfile(@PathVariable Long id) throws JobPortalException {
        log.info("Fetching profile with id {}", id);
        ProfileDTO profile = profileService.getProfile(id);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<ProfileDTO> updateProfile(
            @PathVariable Long id,
            @RequestBody @Valid ProfileDTO profileDTO
    ) throws JobPortalException {
        log.info("Updating profile with id {}", id);
        profileDTO.setId(id);
        ProfileDTO profile = profileService.updateProfile(profileDTO);
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/{profileId}/save-job/{jobId}")
    public ResponseEntity<String> saveJob(
            @PathVariable Long profileId,
            @PathVariable Long jobId) throws JobPortalException {

        profileService.saveJob(profileId, jobId);
        return ResponseEntity.ok("Job saved successfully");
    }
}
