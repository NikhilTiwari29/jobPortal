package com.jobPortal;

import com.jobPortal.dto.LoginDTO;
import com.jobPortal.dto.UserDTO;
import com.jobPortal.entity.User;
import com.jobPortal.enums.AccountType;
import com.jobPortal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "100000")
@Import(TestContainerConfiguration.class)
public class UserControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;
    private UserDTO userDto;
    private LoginDTO loginDto;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        user = User.builder()
                .userName("nikhil")
                .email("nikhiltiwarip29@gmail.com")
                .password("NikTiwari@1234")
                .accountType(AccountType.EMPLOYER)
                .build();

        userDto = UserDTO.builder()
                .userName("nikhil")
                .email("nikhiltiwarip29@gmail.com")
                .password("NikTiwari@1234")
                .accountType(AccountType.EMPLOYER)
                .build();

        loginDto = LoginDTO.builder()
                .email("nikhiltiwarip29@gmail.com")
                .password("NikTiwari@1234")
                .build();
    }

    @Test
    void registerUser_success() {
        webTestClient.post()
                .uri("/users/register")
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.email").isEqualTo(userDto.getEmail())
                .jsonPath("$.userName").isEqualTo(userDto.getUserName())
                .jsonPath("$.accountType").isEqualTo("EMPLOYER");

        User savedUser = userRepository.findByEmail(userDto.getEmail()).orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUserName()).isEqualTo(userDto.getUserName());
    }

    @Test
    void registerUser_duplicateEmail() {
        userRepository.save(user);

        userDto.setEmail(user.getEmail());

        webTestClient.post()
                .uri("/users/register")
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.errorMessages[0]").isEqualTo("An account with this email already exists. Try signing in instead.")
                .jsonPath("$.errorCode").isEqualTo(409);

        assertThat(userRepository.findByEmail(userDto.getEmail())).isPresent();
    }

    @Test
    void registerUser_InvalidEmailFormat() {
        userDto.setEmail("invalidemail");

        webTestClient.post()
                .uri("/users/register")
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errorMessages[0]").isEqualTo("Enter a valid email address (example: name@domain.com).")
                .jsonPath("$.errorCode").isEqualTo(400);

        assertThat(userRepository.findByEmail(userDto.getEmail())).isEmpty();
    }

    @Test
    void registerUser_WeakInvalidPassword() {
        userDto.setPassword("pass");

        webTestClient.post()
                .uri("/users/register")
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errorMessages").isArray()
                .jsonPath("$.errorMessages").value(messages -> {
                    @SuppressWarnings("unchecked")
                    var errorList = (java.util.List<String>) messages;
                    assertThat(errorList).contains(
                            "Password must be at least 8 characters long.",
                            "Password must include at least one uppercase letter, one lowercase letter, one number, and one special character."
                    );
                })
                .jsonPath("$.errorCode").isEqualTo(400);

        assertThat(userRepository.findByEmail(userDto.getEmail())).isEmpty();
    }


    @Test
    void registerUser_missingRequiredField() {
        userDto.setEmail(null);

        webTestClient.post()
                .uri("/users/register")
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errorMessages[0]").isEqualTo("Please enter your email address.")
                .jsonPath("$.errorCode").isEqualTo(400);

        assertThat(userRepository.findByEmail(userDto.getEmail())).isEmpty();
    }

    @Test
    void registerUser_withDifferentAccountTypes() {
        // EMPLOYER
        userDto.setAccountType(AccountType.EMPLOYER);

        webTestClient.post()
                .uri("/users/register")
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.accountType").isEqualTo("EMPLOYER");

        User employerUser = userRepository.findByEmail(userDto.getEmail()).orElse(null);
        assertThat(employerUser).isNotNull();
        assertThat(employerUser.getAccountType()).isEqualTo(AccountType.EMPLOYER);

        // JOB_SEEKER
        userDto.setEmail("jobseeker@example.com");
        userDto.setAccountType(AccountType.APPLICANT);

        webTestClient.post()
                .uri("/users/register")
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.accountType").isEqualTo("APPLICANT");

        User testUser = userRepository.findByEmail("jobseeker@example.com").orElse(null);
        assertThat(testUser).isNotNull();
        assertThat(testUser.getAccountType()).isEqualTo(AccountType.APPLICANT);
    }

    @Test
    void registerUser_caseInsensitiveEmail_shouldConflict() {
        userRepository.save(user);

        userDto.setEmail("NikhilTiwarip29@GMAIL.com");

        webTestClient.post()
                .uri("/users/register")
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.errorMessages[0]").isEqualTo("An account with this email already exists. Try signing in instead.")
                .jsonPath("$.errorCode").isEqualTo(409);

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void loginUser_success(){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        webTestClient.post()
                .uri("/users/login")
                .bodyValue(loginDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.email").isEqualTo(loginDto.getEmail());

        User testUser = userRepository.findByEmail(loginDto.getEmail()).orElse(null);
        assertThat(testUser).isNotNull();
    }

    @Test
    void loginUser_emailNotFound(){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        loginDto.setEmail("wrong@gmail.com");

        webTestClient.post()
                .uri("/users/login")
                .bodyValue(loginDto)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.errorMessages").isEqualTo("No account found with this email. Please check and try again.")
                .jsonPath("$.errorCode").isEqualTo(409);

        User testUser = userRepository.findByEmail(loginDto.getEmail()).orElse(null);
        assertThat(testUser).isNull();
    }

    @Test
    void loginUser_wrongPassword(){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        loginDto.setPassword("wrongPass@1234");

        webTestClient.post()
                .uri("/users/login")
                .bodyValue(loginDto)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.errorMessages").isEqualTo("The email or password you entered is incorrect. Please try again.")
                .jsonPath("$.errorCode").isEqualTo(409);

    }

    @Test
    void loginUser_inValidEmailFormat(){

        loginDto.setEmail("invalidEmailFormat.com");

        webTestClient.post()
                .uri("/users/login")
                .bodyValue(loginDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errorMessages").isEqualTo("Enter a valid email address (example: name@domain.com).")
                .jsonPath("$.errorCode").isEqualTo(400);

    }

    @Test
    void loginUser_inValidPasswordFormat(){

        loginDto.setPassword("pass");

        webTestClient.post()
                .uri("/users/login")
                .bodyValue(loginDto)
                .exchange()
                .expectBody()
                .jsonPath("$.errorMessages").isArray()
                .jsonPath("$.errorMessages").value(messages -> {
                    @SuppressWarnings("unchecked")
                    var errorList = (java.util.List<String>) messages;
                    assertThat(errorList).contains(
                            "Password must be at least 8 characters long.",
                            "Password must include at least one uppercase letter, one lowercase letter, one number, and one special character."
                    );
                })
                .jsonPath("$.errorCode").isEqualTo(400);

    }
}
