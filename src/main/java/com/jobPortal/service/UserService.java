package com.jobPortal.service;

import com.jobPortal.dto.LoginDTO;
import com.jobPortal.dto.UserDTO;
import com.jobPortal.exception.JobPortalException;

public interface UserService {
    UserDTO registerUser(UserDTO userDto) throws JobPortalException;

    UserDTO loginUser(LoginDTO loginDto) throws JobPortalException;
}
