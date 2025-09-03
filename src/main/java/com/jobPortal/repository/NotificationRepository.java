package com.jobPortal.repository;

import com.jobPortal.entity.Notification;
import com.jobPortal.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findByUserIdAndStatus(Long userId, NotificationStatus status);
}
