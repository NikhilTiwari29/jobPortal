package com.jobPortal.security;

import com.jobPortal.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtUserDetails implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private AccountType accountType;
    private Collection<?extends GrantedAuthority> authorities;

    @Override
    public String getUsername() {
        // Spring Security still needs a "username"
        // we return the id as a string
        return String.valueOf(id);
    }
}
