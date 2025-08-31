package com.jobPortal.repository;

import com.jobPortal.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpRepository extends JpaRepository<Otp, UUID> {
    Optional<Otp> findByEmail(String email);

    void deleteAllByCreationTimeBefore(LocalDateTime localDateTime);
}
