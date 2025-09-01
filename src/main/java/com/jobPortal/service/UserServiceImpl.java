package com.jobPortal.service;

import com.jobPortal.dto.ChangePasswordRequestDTO;
import com.jobPortal.dto.LoginDTO;
import com.jobPortal.dto.UserDTO;
import com.jobPortal.entity.User;
import com.jobPortal.exception.JobPortalException;
import com.jobPortal.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    private final ProfileService profileService;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, ProfileService profileService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.profileService = profileService;
    }

    @Override
    public UserDTO registerUser(UserDTO userDto) throws JobPortalException {
        log.info("Attempting to register user with email={}", userDto.getEmail());

        if (userRepository.existsByEmail(userDto.getEmail())) {
            log.error("user already exists with email {}",userDto.getEmail());
            throw new JobPortalException("user.already.exists");
        }

        User user = modelMapper.map(userDto, User.class);
        log.debug("Mapped UserDTO to User entity: {}", user);

        // hashing password before saving
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        log.debug("Password encoded for email={}", userDto.getEmail());

        User savedUser = userRepository.save(user);

        // delegate profile creation
        profileService.createProfileForUser(savedUser);

        log.info("User successfully registered with id={} and email={}", savedUser.getId(), savedUser.getEmail());

        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    public UserDTO loginUser(LoginDTO loginDto) throws JobPortalException {
        log.info("Login attempt started for email={}", loginDto.getEmail());

        // Fetch user by email
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed: no user found with email={}", loginDto.getEmail());
                    return new JobPortalException("user.does.not.exists");
                });

        // Validate password
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            log.warn("Login failed: invalid credentials for email={}", loginDto.getEmail());
            throw new JobPortalException("user.invalid.credentials");
        }

        log.info("Login successful for userId={} email={}", user.getId(), user.getEmail());

        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public void changePassword(String email, ChangePasswordRequestDTO request) throws JobPortalException {

        // Fetch user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Password change failed: no user found with email={}", email);
                    return new JobPortalException("user.does.not.exists");
                });

        // Validate password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            log.warn("Password change failed: invalid credentials for email={}", email);
            throw new JobPortalException("user.old.password.incorrect");
        }

        // Validate new password & confirm password are same
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new JobPortalException("user.new.password.confirm.password.do.mot.match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
