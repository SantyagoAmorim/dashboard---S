package com.agency.dashboard.service;

import com.agency.dashboard.domain.AppNotification;
import com.agency.dashboard.repo.AppNotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final AppNotificationRepository appNotificationRepository;

    public NotificationService(AppNotificationRepository appNotificationRepository) {
        this.appNotificationRepository = appNotificationRepository;
    }

    public AppNotification createNotification(
            String title,
            String message,
            String targetSector,
            String targetUser,
            String referenceType,
            Long referenceId
    ) {
        AppNotification notification = new AppNotification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setTargetSector(targetSector);
        notification.setTargetUser(targetUser);
        notification.setReferenceType(referenceType);
        notification.setReferenceId(referenceId);
        notification.setRead(false);

        return appNotificationRepository.save(notification);
    }

    public List<AppNotification> findAll() {
        return appNotificationRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<AppNotification> findUnread() {
        return appNotificationRepository.findByReadFalseOrderByCreatedAtDesc();
    }

    public long unreadCount() {
        return appNotificationRepository.countByReadFalse();
    }

    public void markAsRead(Long id) {
        appNotificationRepository.findById(id).ifPresent(notification -> {
            notification.setRead(true);
            appNotificationRepository.save(notification);
        });
    }

    public void markAllAsRead() {
        List<AppNotification> notifications = appNotificationRepository.findByReadFalseOrderByCreatedAtDesc();
        for (AppNotification notification : notifications) {
            notification.setRead(true);
        }
        appNotificationRepository.saveAll(notifications);
    }
}