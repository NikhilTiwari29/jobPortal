package com.jobPortal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificationDTO {
    private Long id;

    @NotBlank(message = "{user.certificateName.required}")
    private String name;
    private String issuer;
    private String issueDate;
    private String certificateId;
}
