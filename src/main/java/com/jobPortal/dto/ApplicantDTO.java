package com.jobPortal.dto;

import com.jobPortal.enums.ApplicationStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicantDTO {

    private Long applicantId;

    @NotBlank(message = "{applicant.name.required}")
    @Size(max = 255, message = "{applicant.name.size}")
    private String name;

    @NotBlank(message = "{applicant.email.required}")
    @Email(message = "{applicant.email.valid}")
    @Size(max = 255, message = "{applicant.email.size}")
    private String email;

    @NotNull(message = "{applicant.phone.required}")
    @Digits(integer = 15, fraction = 0, message = "{applicant.phone.digits}")
    private Long phone;

    @Size(max = 255, message = "{applicant.website.size}")
    @Pattern(
            regexp = "^(https?://.*)?$",
            message = "{applicant.website.valid}"
    )
    private String website;

    @NotBlank(message = "{applicant.resume.required}")
    private String resume; // could be file path / URL

    @Size(max = 2000, message = "{applicant.coverLetter.size}")
    private String coverLetter;

    private LocalDateTime timestamp;

    private ApplicationStatus applicationStatus;

    private LocalDateTime interviewTime;
}
