package com.jobPortal.service;

import com.jobPortal.dto.ChangePasswordRequestDTO;
import com.jobPortal.dto.LoginDTO;
import com.jobPortal.dto.NotificationDTO;
import com.jobPortal.dto.UserDTO;
import com.jobPortal.entity.User;
import com.jobPortal.exception.JobPortalException;
import com.jobPortal.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final ProfileService profileService;
    private final NotificationService notificationService; // ✅ inject notification service

    public UserServiceImpl(UserRepository userRepository,
                           ModelMapper modelMapper,
                           PasswordEncoder passwordEncoder,
                           ProfileService profileService,
                           NotificationService notificationService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.profileService = profileService;
        this.notificationService = notificationService;
    }

    @Override
    public UserDTO registerUser(UserDTO userDto) throws JobPortalException {
        log.info("Attempting to register user with email={}", userDto.getEmail());

        if (userRepository.existsByEmail(userDto.getEmail())) {
            log.error("User already exists with email {}", userDto.getEmail());
            throw new JobPortalException("user.already.exists");
        }

        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User savedUser = userRepository.save(user);

        // delegate profile creation
        profileService.createProfileForUser(savedUser);

        log.info("User successfully registered with id={} and email={}", savedUser.getId(), savedUser.getEmail());

        // ✅ Send welcome notification
        NotificationDTO notification = NotificationDTO.builder()
                .userId(savedUser.getId())
                .message("Welcome " + savedUser.getUserName() + "! Your account has been created.")
                .action("User Registered")
                .timestamp(LocalDateTime.now())
                .build();

        notificationService.sendNotification(notification);

        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    public UserDTO loginUser(LoginDTO loginDto) throws JobPortalException {
        log.info("Login attempt started for email={}", loginDto.getEmail());

        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed: no user found with email={}", loginDto.getEmail());
                    return new JobPortalException("user.does.not.exists");
                });

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            log.warn("Login failed: invalid credentials for email={}", loginDto.getEmail());
            throw new JobPortalException("user.invalid.credentials");
        }

        log.info("Login successful for userId={} email={}", user.getId(), user.getEmail());

        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public void changePassword(String email, ChangePasswordRequestDTO request) throws JobPortalException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new JobPortalException("user.does.not.exists"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new JobPortalException("user.old.password.incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new JobPortalException("user.new.password.confirm.password.do.mot.match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // ✅ Send password change notification
        NotificationDTO notification = NotificationDTO.builder()
                .userId(user.getId())
                .message("Your password was changed successfully.")
                .action("Password Change")
                .timestamp(LocalDateTime.now())
                .build();

        notificationService.sendNotification(notification);

        log.info("Password updated successfully for userId={} email={}", user.getId(), user.getEmail());
    }
}
