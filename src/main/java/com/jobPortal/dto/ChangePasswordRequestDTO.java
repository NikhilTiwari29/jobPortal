package com.jobPortal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequestDTO {

    @NotBlank(message = "{user.old.password.required}")
    private String oldPassword;

    @NotBlank(message = "{user.new.password.required}")
    private String newPassword;

    @NotBlank(message = "{user.confirm.password.required}")
    private String confirmPassword;
}
