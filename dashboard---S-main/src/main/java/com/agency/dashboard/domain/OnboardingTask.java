package com.agency.dashboard.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class OnboardingTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OnboardingTaskType taskType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OnboardingTaskStatus status = OnboardingTaskStatus.PENDING;

    private String responsible;

    private LocalDate dueDate;

    @Column(length = 2000)
    private String notes;

    @Column(nullable = false)
    private Integer sortOrder;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public OnboardingTask() {
    }

    public Long getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public OnboardingTaskType getTaskType() {
        return taskType;
    }

    public OnboardingTaskStatus getStatus() {
        return status;
    }

    public String getResponsible() {
        return responsible;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getNotes() {
        return notes;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setTaskType(OnboardingTaskType taskType) {
        this.taskType = taskType;
    }

    public void setStatus(OnboardingTaskStatus status) {
        this.status = status;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}