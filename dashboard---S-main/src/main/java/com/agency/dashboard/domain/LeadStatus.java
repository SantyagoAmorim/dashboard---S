package com.agency.dashboard.domain;

public enum LeadStatus {

    LEAD("Lead"),
    DIAGNOSTICO("Diagnóstico"),
    PROPOSTA("Proposta enviada"),
    NEGOCIACAO("Negociação"),
    FECHADO("Fechado"),
    PERDIDO("Perdido");

    private final String label;

    LeadStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}