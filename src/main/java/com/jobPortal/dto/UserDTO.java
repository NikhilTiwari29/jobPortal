package com.jobPortal.dto;

import com.jobPortal.enums.AccountType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private UUID id;

    @NotBlank(message = "{user.username.required}")
    @Size(min = 2, max = 50, message = "{user.username.size}")
    private String userName;

    @NotBlank(message = "{user.email.required}")
    @Email(message = "{user.email.invalid}")
    private String email;

    @NotBlank(message = "{user.password.required}")
    @Size(min = 8, max = 100, message = "{user.password.size}")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "{user.password.pattern}"
    )
    private String password;

    @NotNull(message = "{user.accountType.required}")
    private AccountType accountType;
}
