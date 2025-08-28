package com.jobPortal.controller;

import com.jobPortal.dto.SendOtpRequestDTO;
import com.jobPortal.dto.VerifyOtpRequestDto;
import com.jobPortal.dto.ResponseDTO;
import com.jobPortal.exception.JobPortalException;
import com.jobPortal.service.OtpService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final OtpService otpService;

    public AuthController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ResponseDTO> sendOtp(@RequestBody SendOtpRequestDTO sendOtpRequestDTO) throws JobPortalException, MessagingException {
        otpService.generateAndSendOtp(sendOtpRequestDTO.getEmail());
        return new ResponseEntity<>(new ResponseDTO("OTP sent successfully"), HttpStatus.OK);
    }

    @PostMapping("/verify/otp")
    public ResponseEntity<ResponseDTO> verifyOtp(@RequestBody VerifyOtpRequestDto verifyOtpRequestDto) throws JobPortalException, MessagingException {
        otpService.verifyOtp(verifyOtpRequestDto.getEmail(),verifyOtpRequestDto.getOtpCode());
        return new ResponseEntity<>(new ResponseDTO("OTP verified successfully"), HttpStatus.OK);
    }
}
