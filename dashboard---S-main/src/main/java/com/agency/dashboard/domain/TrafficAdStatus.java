package com.agency.dashboard.domain;

public enum TrafficAdStatus {

    ESPERANDO_APROVACAO_CRIATIVO("Esperando aprovação do criativo"),
    PENDENTE("Pendente"),
    EM_ANDAMENTO("Em andamento"),
    CONCLUIDO("Concluído");

    private final String label;

    TrafficAdStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}