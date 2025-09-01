-- Job table
CREATE TABLE job (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_title VARCHAR(255) NOT NULL,
    company VARCHAR(255),
    about TEXT,
    experience VARCHAR(255),
    job_type VARCHAR(100),
    location VARCHAR(255),
    package_offered BIGINT,
    post_time TIMESTAMP,
    description TEXT,
    job_status VARCHAR(50)
);

-- Job skills (ElementCollection)
CREATE TABLE job_skills (
    job_id BIGINT NOT NULL,
    skill VARCHAR(255) NOT NULL,
    CONSTRAINT fk_job_skills FOREIGN KEY (job_id)
        REFERENCES job(id)
        ON DELETE CASCADE
);

-- Applicants
CREATE TABLE applicant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    time_stamp TIMESTAMP,
    applicant_status VARCHAR(50),
    job_id BIGINT NOT NULL,
    CONSTRAINT fk_applicant_job FOREIGN KEY (job_id)
        REFERENCES job(id)
        ON DELETE CASCADE
);
