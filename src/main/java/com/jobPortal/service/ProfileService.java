package com.jobPortal.service;

import com.jobPortal.dto.ProfileDTO;
import com.jobPortal.entity.User;
import com.jobPortal.exception.JobPortalException;

public interface ProfileService {
    void createProfileForUser(User user);
    ProfileDTO getProfile(Long id) throws JobPortalException;
    ProfileDTO updateProfile(ProfileDTO profileDTO) throws JobPortalException;
}
