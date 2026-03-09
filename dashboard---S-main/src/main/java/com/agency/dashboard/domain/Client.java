package com.agency.dashboard.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    private String company;
    private String whatsapp;
    private String email;
    private String accountManager;

    @Enumerated(EnumType.STRING)
    private ClientPlan plan;

    @Enumerated(EnumType.STRING)
    private ClientSector sector;

    private String squad;

    @Column(length = 2000)
    private String notes;

    private LocalDate onboardingMeetingDate;

    private boolean onboardingCompleted;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Client() {
    }

    public Client(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCompany() {
        return company;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public String getEmail() {
        return email;
    }

    public String getAccountManager() {
        return accountManager;
    }

    public ClientPlan getPlan() {
        return plan;
    }

    public ClientSector getSector() {
        return sector;
    }

    public String getSquad() {
        return squad;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDate getOnboardingMeetingDate() {
        return onboardingMeetingDate;
    }

    public boolean isOnboardingCompleted() {
        return onboardingCompleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAccountManager(String accountManager) {
        this.accountManager = accountManager;
    }

    public void setPlan(ClientPlan plan) {
        this.plan = plan;
    }

    public void setSector(ClientSector sector) {
        this.sector = sector;
    }

    public void setSquad(String squad) {
        this.squad = squad;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setOnboardingMeetingDate(LocalDate onboardingMeetingDate) {
        this.onboardingMeetingDate = onboardingMeetingDate;
    }

    public void setOnboardingCompleted(boolean onboardingCompleted) {
        this.onboardingCompleted = onboardingCompleted;
    }

    @Override
    public String toString() {
        return name;
    }
}