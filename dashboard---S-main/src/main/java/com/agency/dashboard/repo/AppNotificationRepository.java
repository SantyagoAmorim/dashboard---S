package com.agency.dashboard.repo;

import com.agency.dashboard.domain.AppNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppNotificationRepository extends JpaRepository<AppNotification, Long> {

    long countByReadFalse();

    List<AppNotification> findAllByOrderByCreatedAtDesc();

    List<AppNotification> findByReadFalseOrderByCreatedAtDesc();
}