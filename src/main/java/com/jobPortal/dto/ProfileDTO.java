package com.jobPortal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    private Long id;

    @NotBlank(message = "{user.profile.name.required}")
    private String name;

    private String resume;

    @Email(message = "{user.email.invalid}")
    @NotBlank(message = "{user.email.required}")
    private String email;

    private String jobTitle;
    private String company;
    private String location;

    @Size(max = 500, message = "{user.profile.about.length}")
    private String about;

    private List<String> skills;

    private List<ExperienceDTO> experiences;
    private List<CertificationDTO> certifications;
}
