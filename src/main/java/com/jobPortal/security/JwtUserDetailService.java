package com.jobPortal.security;

import com.jobPortal.dto.UserDTO;
import com.jobPortal.exception.JobPortalException;
import com.jobPortal.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class JwtUserDetailService implements UserDetailsService {

    private final UserService userService;

    public JwtUserDetailService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        UserDTO user;
        try {
            user = userService.getUserById(Long.parseLong(id));
        } catch (JobPortalException e) {
            throw new RuntimeException(e);
        }
        return new JwtUserDetails(user.getId(), user.getUserName(), user.getPassword(), user.getAccountType(), new ArrayList<>());
    }
}
