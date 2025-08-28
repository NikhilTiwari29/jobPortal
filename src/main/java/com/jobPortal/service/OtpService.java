package com.jobPortal.service;

import com.jobPortal.exception.JobPortalException;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public interface OtpService {
    void generateAndSendOtp(String email) throws JobPortalException, MessagingException;

    void verifyOtp(String email, String otpCode) throws JobPortalException;
}
