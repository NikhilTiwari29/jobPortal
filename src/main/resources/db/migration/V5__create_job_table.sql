-- ================================
-- Create table: job
-- ================================
CREATE TABLE job (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_title VARCHAR(255),
    company VARCHAR(255),
    about VARCHAR(2000),
    experience VARCHAR(255),
    job_type VARCHAR(255),
    location VARCHAR(255),
    package_offered BIGINT,
    post_time TIMESTAMP,
    description VARCHAR(5000),
    job_status VARCHAR(50),
    posted_by BIGINT
);

-- ================================
-- Create table: applicants
-- ================================
CREATE TABLE applicant (
    applicant_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    phone BIGINT,
    website VARCHAR(255),
    resume TEXT,
    cover_letter VARCHAR(2000),
    timestamp TIMESTAMP,
    application_status VARCHAR(50),
    interview_time TIMESTAMP,
    job_id BIGINT,
    CONSTRAINT fk_applicant_job FOREIGN KEY (job_id) REFERENCES job(id) ON DELETE CASCADE
);

-- ================================
-- Create table: job_skills (ElementCollection)
-- ================================
CREATE TABLE job_skills (
    job_id BIGINT NOT NULL,
    skills_required VARCHAR(255) NOT NULL,
    CONSTRAINT fk_job_skill FOREIGN KEY (job_id) REFERENCES job(id) ON DELETE CASCADE
);
