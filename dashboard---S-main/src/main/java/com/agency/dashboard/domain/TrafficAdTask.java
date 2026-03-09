package com.agency.dashboard.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class TrafficAdTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrafficAdStatus status = TrafficAdStatus.PENDENTE;

    private String responsible;

    private LocalDate dueDate;

    private String mediaUrl;

    @Column(length = 2000)
    private String designNotes;

    @Column(length = 2000)
    private String trafficNotes;

    private String createdBySector;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public TrafficAdTask() {
    }

    public Long getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TrafficAdStatus getStatus() {
        return status;
    }

    public String getResponsible() {
        return responsible;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getDesignNotes() {
        return designNotes;
    }

    public String getTrafficNotes() {
        return trafficNotes;
    }

    public String getCreatedBySector() {
        return createdBySector;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TrafficAdStatus status) {
        this.status = status;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public void setDesignNotes(String designNotes) {
        this.designNotes = designNotes;
    }

    public void setTrafficNotes(String trafficNotes) {
        this.trafficNotes = trafficNotes;
    }

    public void setCreatedBySector(String createdBySector) {
        this.createdBySector = createdBySector;
    }
}