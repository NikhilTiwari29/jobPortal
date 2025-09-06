package com.jobPortal.controller;

import com.jobPortal.dto.ChangePasswordRequestDTO;
import com.jobPortal.dto.ResponseDTO;
import com.jobPortal.dto.UserDTO;
import com.jobPortal.exception.JobPortalException;
import com.jobPortal.dto.AuthenticationRequest;
import com.jobPortal.dto.AuthenticationResponse;
import com.jobPortal.security.JwtService;
import com.jobPortal.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtService jwtService1) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService1;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody @Valid UserDTO userDto) throws JobPortalException {
        log.info("Register request received for email={}", userDto.getEmail());
        UserDTO response = userService.registerUser(userDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws JobPortalException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getId(), authenticationRequest.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new JobPortalException("Incorrect username or password");
        }


        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getId());
        final String jwt = jwtService.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    //TODO: Need to change this using jwt rather than sending email
    @PostMapping("/change-password")
    public ResponseEntity<ResponseDTO> changePassword(
            @RequestParam String email,
            @RequestBody @Valid ChangePasswordRequestDTO request
    ) throws JobPortalException {
        userService.changePassword(email,request);
        return new ResponseEntity<>(new ResponseDTO("Password changed successfully"),HttpStatus.OK);
    }
}
