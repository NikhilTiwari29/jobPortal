package com.jobPortal.service;

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

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
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
}
