package com.jobPortal.service;

import com.jobPortal.dto.NotificationDTO;
import com.jobPortal.entity.Notification;
import com.jobPortal.enums.NotificationStatus;
import com.jobPortal.exception.JobPortalException;
import com.jobPortal.repository.NotificationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository, ModelMapper modelMapper) {
        this.notificationRepository = notificationRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void sendNotification(NotificationDTO notificationDTO) throws JobPortalException {
        try {
            notificationDTO.setStatus(NotificationStatus.UNREAD);
            notificationDTO.setTimestamp(LocalDateTime.now());

            Notification notification = modelMapper.map(notificationDTO, Notification.class);
            notificationRepository.save(notification);
        } catch (Exception e) {
            throw new JobPortalException("notification.send.failed");
        }
    }

    @Override
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndStatus(userId, NotificationStatus.UNREAD);
    }

    @Override
    public void readNotification(Long id) throws JobPortalException {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new JobPortalException("notification.not.found"));

        notification.setStatus(NotificationStatus.READ);
        notificationRepository.save(notification);
    }
}
