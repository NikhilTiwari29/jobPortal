-- Profile main table
CREATE TABLE profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255),
    job_title VARCHAR(255),
    company VARCHAR(255),
    location VARCHAR(255),
    about TEXT
);

-- Skills (ElementCollection)
CREATE TABLE profile_skills (
    profile_id BIGINT NOT NULL,
    skill VARCHAR(255) NOT NULL,
    CONSTRAINT fk_profile_skills FOREIGN KEY (profile_id)
        REFERENCES profile(id)
        ON DELETE CASCADE
);

-- Experience (OneToMany)
CREATE TABLE experience (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    company VARCHAR(255),
    location VARCHAR(255),
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    working BOOLEAN,
    description TEXT,
    profile_id BIGINT NOT NULL,
    CONSTRAINT fk_experience_profile FOREIGN KEY (profile_id)
        REFERENCES profile(id)
        ON DELETE CASCADE
);

-- Certification (OneToMany)
CREATE TABLE certification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    issuer VARCHAR(255),
    issue_date TIMESTAMP,
    certificate_id VARCHAR(255),
    profile_id BIGINT NOT NULL,
    CONSTRAINT fk_certification_profile FOREIGN KEY (profile_id)
        REFERENCES profile(id)
        ON DELETE CASCADE
);
