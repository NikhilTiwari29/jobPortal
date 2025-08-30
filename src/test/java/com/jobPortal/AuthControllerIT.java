package com.jobPortal;

import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.jobPortal.dto.SendOtpRequestDTO;
import com.jobPortal.dto.VerifyOtpRequestDto;
import com.jobPortal.entity.Otp;
import com.jobPortal.entity.User;
import com.jobPortal.enums.AccountType;
import com.jobPortal.repository.OtpRepository;
import com.jobPortal.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "100000")
@Import(TestContainerConfiguration.class)
@ActiveProfiles("test")
class AuthControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpRepository otpRepository;

    private User user;
    private static GreenMail greenMail;

    @BeforeAll
    static void startMailServer() {
        greenMail = new GreenMail(ServerSetupTest.SMTP); // fake SMTP on port 3025
        greenMail.start();
    }

    @AfterAll
    static void stopMailServer() {
        greenMail.stop();
    }

    @BeforeEach
    void setUp() throws FolderException {
        otpRepository.deleteAll();
        userRepository.deleteAll();

        user = User.builder()
                .userName("nikhil")
                .email("nikhiltiwarip29@gmail.com")
                .password("NikTiwari@1234")
                .accountType(AccountType.EMPLOYER)
                .build();

        userRepository.save(user);
        greenMail.purgeEmailFromAllMailboxes(); // clear old mails before each test
    }

    @Test
    void sendOtp_success() throws Exception {
        SendOtpRequestDTO request = new SendOtpRequestDTO(user.getEmail());

        webTestClient.post()
                .uri("/auth/send-otp")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("OTP sent successfully");

        // ✅ Verify OTP is saved in DB
        assertThat(otpRepository.findByEmail(user.getEmail())).isPresent();

        // ✅ Verify email was delivered to fake SMTP
        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertThat(messages).hasSize(1);
        assertThat(messages[0].getAllRecipients()[0].toString()).isEqualTo(user.getEmail());
        assertThat(messages[0].getSubject()).contains("OTP"); // subject check
    }

    @Test
    void sendOtp_userNotFound() {
        SendOtpRequestDTO request = new SendOtpRequestDTO("unknown@example.com");

        webTestClient.post()
                .uri("/auth/send-otp")
                .bodyValue(request)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.errorMessages[0]").isEqualTo("No account found with this email. Please check and try again.");

        // ✅ No email should be sent
        assertThat(greenMail.getReceivedMessages()).isEmpty();
    }

    @Test
    void verifyOtp_success() {
        // Arrange
        Otp otp = Otp.builder()
                .email(user.getEmail())
                .otpCode("123456")
                .creationTime(LocalDateTime.now())
                .build();
        otpRepository.save(otp);

        VerifyOtpRequestDto request = new VerifyOtpRequestDto(user.getEmail(), "123456");

        webTestClient.post()
                .uri("/auth/verify/otp")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("OTP verified successfully");

        assertThat(otpRepository.findByEmail(user.getEmail())).isEmpty();
    }

    @Test
    void verifyOtp_expired() {
        Otp otp = Otp.builder()
                .email(user.getEmail())
                .otpCode("123456")
                .creationTime(LocalDateTime.now().minusMinutes(10))
                .build();
        otpRepository.save(otp);

        VerifyOtpRequestDto request = new VerifyOtpRequestDto(user.getEmail(), "123456");

        webTestClient.post()
                .uri("/auth/verify/otp")
                .bodyValue(request)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.errorMessages[0]").isEqualTo("OTP is expired");
    }

    @Test
    void verifyOtp_incorrect() {
        Otp otp = Otp.builder()
                .email(user.getEmail())
                .otpCode("123456")
                .creationTime(LocalDateTime.now())
                .build();
        otpRepository.save(otp);

        VerifyOtpRequestDto request = new VerifyOtpRequestDto(user.getEmail(), "000000");

        webTestClient.post()
                .uri("/auth/verify/otp")
                .bodyValue(request)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.errorMessages[0]").isEqualTo("The OTP you entered is incorrect. Please try again.");
    }

    @Test
    void verifyOtp_notFound() {
        VerifyOtpRequestDto request = new VerifyOtpRequestDto(user.getEmail(), "123456");

        webTestClient.post()
                .uri("/auth/verify/otp")
                .bodyValue(request)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.errorMessages[0]").isEqualTo("We couldn't find a valid OTP. Please request a new one.");
    }
}
