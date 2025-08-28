package com.jobPortal.service;

import com.jobPortal.entity.Otp;
import com.jobPortal.exception.JobPortalException;
import com.jobPortal.repository.OtpRepository;
import com.jobPortal.repository.UserRepository;
import com.jobPortal.utility.Utils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.jobPortal.utility.Utils.htmlContent;
import static com.jobPortal.utility.Utils.sendOtpMail;

@Service
@Slf4j
public class OtpServiceImpl implements OtpService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final OtpRepository otpRepository;

    public OtpServiceImpl(JavaMailSender mailSender, UserRepository userRepository, OtpRepository otpRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
    }

    @Override
    public void generateAndSendOtp(String email) throws JobPortalException {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new JobPortalException("user.does.not.exists"));

        String generatedOTP = Utils.generateOTP();

        try {
            // 1. send mail first
            sendOtpMail(email, generatedOTP, mailSender);

            // 2. save OTP only if mail succeeded
            saveOtp(email, generatedOTP);

        } catch (MessagingException ex) {
            log.error("Failed to send OTP email to {}", email, ex);
            throw new JobPortalException("otp.mail.failed");
        }
    }

    @Transactional
    private void saveOtp(String email, String otp) {
        Otp otpEntity = otpRepository.findByEmail(email)
                .map(existing -> {
                    existing.setOtpCode(otp);
                    existing.setCreationTime(LocalDateTime.now());
                    return existing;
                })
                .orElseGet(() -> Otp.builder()
                        .email(email)
                        .otpCode(otp)
                        .creationTime(LocalDateTime.now())
                        .build()
                );

        otpRepository.save(otpEntity);
    }


    @Override
    @Transactional
    public void verifyOtp(String email, String otpCode) throws JobPortalException {
        Otp otp = otpRepository.findByEmail(email)
                .orElseThrow(() -> new JobPortalException("otp.not.found"));

        // check expiry (5 mins)
        if (otp.getCreationTime().plusMinutes(5).isBefore(LocalDateTime.now())) {
            otpRepository.delete(otp); // cleanup expired OTP
            throw new JobPortalException("otp.expired");
        }

        // verify otp code
        if (!otp.getOtpCode().equals(otpCode)) {
            throw new JobPortalException("otp.incorrect");
        }

        // delete after successful verification
        otpRepository.delete(otp);

        log.debug("OTP verified successfully for email={}", email);
    }

}
