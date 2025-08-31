package com.jobPortal.service;

import com.jobPortal.dto.ChangePasswordRequestDTO;
import com.jobPortal.dto.LoginDTO;
import com.jobPortal.dto.UserDTO;
import com.jobPortal.exception.JobPortalException;
import jakarta.validation.Valid;

public interface UserService {
    UserDTO registerUser(UserDTO userDto) throws JobPortalException;

    UserDTO loginUser(LoginDTO loginDto) throws JobPortalException;

    void changePassword(String email, @Valid ChangePasswordRequestDTO request) throws JobPortalException;
}
