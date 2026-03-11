package com.agency.dashboard.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_notification")
public class AppNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String message;

    private String targetSector;

    private String targetUser;

    @Column(nullable = false)
    private boolean read = false;

    private String referenceType;

    private Long referenceId;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public AppNotification() {
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getTargetSector() {
        return targetSector;
    }

    public String getTargetUser() {
        return targetUser;
    }

    public boolean isRead() {
        return read;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTargetSector(String targetSector) {
        this.targetSector = targetSector;
    }

    public void setTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }
}