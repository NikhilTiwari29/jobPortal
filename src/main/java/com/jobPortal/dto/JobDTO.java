package com.jobPortal.dto;

import com.jobPortal.enums.JobStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDTO {

    private Long id;

    @NotBlank(message = "{job.title.required}")
    @Size(max = 255, message = "{job.title.size}")
    private String jobTitle;

    @NotBlank(message = "{job.company.required}")
    @Size(max = 255, message = "{job.company.size}")
    private String company;

    @Size(max = 2000, message = "{job.about.size}")
    private String about;

    @NotBlank(message = "{job.experience.required}")
    private String experience;

    @NotBlank(message = "{job.type.required}")
    private String jobType;

    @NotBlank(message = "{job.location.required}")
    private String location;

    @NotNull(message = "{job.package.required}")
    @Positive(message = "{job.package.positive}")
    private Long packageOffered;

    private LocalDateTime postTime;

    @NotBlank(message = "{job.description.required}")
    @Size(max = 5000, message = "{job.description.size}")
    private String description;

    @NotEmpty(message = "{job.skills.required}")
    private List<String> skillsRequired;

    @NotNull(message = "{job.status.required}")
    private JobStatus jobStatus;

    private Long postedBy;
}
