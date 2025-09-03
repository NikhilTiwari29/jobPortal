package com.jobPortal.service;

import com.jobPortal.dto.NotificationDTO;
import com.jobPortal.entity.Notification;
import com.jobPortal.exception.JobPortalException;

import java.util.List;

public interface NotificationService {
	void sendNotification(NotificationDTO notificationDTO) throws JobPortalException;
	List<Notification> getUnreadNotifications(Long userId);
	void readNotification(Long id) throws JobPortalException;
}
