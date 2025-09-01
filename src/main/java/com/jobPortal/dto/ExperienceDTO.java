package com.jobPortal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceDTO {
    private Long id;

    @NotBlank(message = "{user.jobExperience.title.required}")
    private String title;

    @NotBlank(message = "{user.jobExperience.companyName.required}")
    private String company;

    private String location;

    @NotBlank(message = "{user.jobExperience.company.startDate}")
    private String startDate;
    private String endDate;
    private Boolean working;
    private String description;
}
