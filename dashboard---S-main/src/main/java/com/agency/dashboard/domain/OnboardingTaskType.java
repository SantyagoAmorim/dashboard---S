package com.agency.dashboard.domain;

public enum OnboardingTaskType {

    BRIEFING_COMERCIAL("Briefing do Comercial"),
    PRIMEIRO_CONTATO("Primeiro Contato"),
    FORMULARIO_PREENCHIDO("Preencheu formulário"),
    LINK_REUNIAO_ENVIADO("Enviar link da reunião onboarding"),
    REUNIAO_ONBOARDING_AGENDADA("Reunião de Onboarding agendada"),
    REUNIAO_FEITA("Reunião Feita"),
    CONFIG_META("Configuração Meta"),
    CONFIG_GOOGLE("Configuração Google"),
    CONFIG_BOT("Configuração Bot"),
    CONFIG_CRM("Configuração CRM"),
    PEDIDO_CRIATIVOS("Criação ou pedido de criativos"),
    SUBIR_PRIMEIROS_ANUNCIOS("Subir primeiros anúncios"),
    VALIDACAO_CRIATIVOS("Validação de criativos"),
    FASE_ESCALA("Fase de Escala");

    private final String label;

    OnboardingTaskType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}