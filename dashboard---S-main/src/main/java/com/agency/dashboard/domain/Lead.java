package com.agency.dashboard.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String company;

    private String phone;

    private String instagram;

    private String source;

    @Enumerated(EnumType.STRING)
    private LeadStatus status = LeadStatus.LEAD;

    private String responsible;

    private Double proposalValue;

    @Column(length = 2000)
    private String notes;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Lead() {}

    public Long getId() { return id; }

    public String getName() { return name; }

    public String getCompany() { return company; }

    public String getPhone() { return phone; }

    public String getInstagram() { return instagram; }

    public String getSource() { return source; }

    public LeadStatus getStatus() { return status; }

    public String getResponsible() { return responsible; }

    public Double getProposalValue() { return proposalValue; }

    public String getNotes() { return notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setName(String name) { this.name = name; }

    public void setCompany(String company) { this.company = company; }

    public void setPhone(String phone) { this.phone = phone; }

    public void setInstagram(String instagram) { this.instagram = instagram; }

    public void setSource(String source) { this.source = source; }

    public void setStatus(LeadStatus status) { this.status = status; }

    public void setResponsible(String responsible) { this.responsible = responsible; }

    public void setProposalValue(Double proposalValue) { this.proposalValue = proposalValue; }

    public void setNotes(String notes) { this.notes = notes; }
}