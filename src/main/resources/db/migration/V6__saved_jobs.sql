CREATE TABLE saved_jobs (
    profile_id BIGINT NOT NULL,
    job_id BIGINT NOT NULL,
    PRIMARY KEY (profile_id, job_id),
    FOREIGN KEY (profile_id) REFERENCES profile(id),
    FOREIGN KEY (job_id) REFERENCES job(id)
);
