package com.jobPortal.entity;

import com.jobPortal.enums.JobStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_title", length = 255)
    private String jobTitle;

    @Column(name = "company", length = 255)
    private String company;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Applicant> applicants;

    @Column(name = "about", length = 2000)
    private String about;

    @Column(name = "experience")
    private String experience;

    @Column(name = "job_type")
    private String jobType;

    @Column(name = "location")
    private String location;

    @Column(name = "package_offered")
    private Long packageOffered;

    @Column(name = "post_time")
    private LocalDateTime postTime;

    @Column(name = "description", length = 5000)
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "job_skills",
            joinColumns = @JoinColumn(name = "job_id")
    )
    private List<String> skillsRequired;

    @Enumerated(EnumType.STRING)
    private JobStatus jobStatus;

    @Column(name = "posted_by")
    private Long postedBy;
}
