package com.jobPortal;

import com.jobPortal.entity.Otp;
import com.jobPortal.entity.User;
import com.jobPortal.enums.AccountType;
import com.jobPortal.exception.JobPortalException;
import com.jobPortal.repository.OtpRepository;
import com.jobPortal.repository.UserRepository;
import com.jobPortal.service.OtpServiceImpl;
import com.jobPortal.utility.Utils;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OtpRepository otpRepository;

    @InjectMocks
    private OtpServiceImpl otpService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userName("Nikhil")
                .email("nikhiltiwarp29@gmail.com")
                .password("NikTiwari@1234")
                .accountType(AccountType.EMPLOYER)
                .build();
    }

    // 1. Happy path
    @Test
    void generateAndSendOtp_success() throws Exception {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        try (MockedStatic<Utils> utilsMock = Mockito.mockStatic(Utils.class)) {
            utilsMock.when(Utils::generateOTP).thenReturn("123456");
            utilsMock.when(() -> Utils.sendOtpMail(eq(user.getEmail()), eq("123456"), any(JavaMailSender.class)))
                    .thenAnswer(invocation -> null); // no-op

            otpService.generateAndSendOtp(user.getEmail());

            verify(userRepository).findByEmail(user.getEmail());
            verify(otpRepository).save(any(Otp.class));
        }
    }

    // 2. User not found
    @Test
    void generateAndSendOtp_userNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> otpService.generateAndSendOtp(user.getEmail()))
                .isInstanceOf(JobPortalException.class)
                .hasMessage("user.does.not.exists");

        verifyNoInteractions(otpRepository);
    }

    // 3. Mail building/sending fails
    @Test
    void generateAndSendOtp_mailFails() throws Exception {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        try (MockedStatic<Utils> utilsMock = Mockito.mockStatic(Utils.class)) {
            utilsMock.when(Utils::generateOTP).thenReturn("123456");
            utilsMock.when(() -> Utils.sendOtpMail(eq(user.getEmail()), eq("123456"), any(JavaMailSender.class)))
                    .thenThrow(new MessagingException("Mail failed"));

            assertThatThrownBy(() -> otpService.generateAndSendOtp(user.getEmail()))
                    .isInstanceOf(JobPortalException.class)
                    .hasMessage("otp.mail.failed");

            verify(otpRepository, never()).save(any());
        }
    }

    // 4. verifyOtp success
    @Test
    void verifyOtp_success() throws Exception {
        Otp otp = Otp.builder()
                .email(user.getEmail())
                .otpCode("123456")
                .creationTime(LocalDateTime.now())
                .build();

        when(otpRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(otp));

        otpService.verifyOtp(user.getEmail(), "123456");

        verify(otpRepository).delete(otp);
    }

    // 5. verifyOtp expired
    @Test
    void verifyOtp_expired() {
        Otp otp = Otp.builder()
                .email(user.getEmail())
                .otpCode("123456")
                .creationTime(LocalDateTime.now().minusMinutes(10)) // expired
                .build();

        when(otpRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(otp));

        assertThatThrownBy(() -> otpService.verifyOtp(user.getEmail(), "123456"))
                .isInstanceOf(JobPortalException.class)
                .hasMessage("otp.expired");

        verify(otpRepository).delete(otp);
    }

    // 6. verifyOtp incorrect
    @Test
    void verifyOtp_incorrectOtp() {
        Otp otp = Otp.builder()
                .email(user.getEmail())
                .otpCode("654321")
                .creationTime(LocalDateTime.now())
                .build();

        when(otpRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(otp));

        assertThatThrownBy(() -> otpService.verifyOtp(user.getEmail(), "123456"))
                .isInstanceOf(JobPortalException.class)
                .hasMessage("otp.incorrect");

        verify(otpRepository, never()).delete(any());
    }

    // 7. verifyOtp not found
    @Test
    void verifyOtp_notFound() {
        when(otpRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> otpService.verifyOtp(user.getEmail(), "123456"))
                .isInstanceOf(JobPortalException.class)
                .hasMessage("otp.not.found");

        verify(otpRepository, never()).delete(any());
    }
}
