package com.agency.dashboard.service;

import com.agency.dashboard.domain.Client;
import com.agency.dashboard.domain.OnboardingTask;
import com.agency.dashboard.domain.OnboardingTaskStatus;
import com.agency.dashboard.domain.OnboardingTaskType;
import com.agency.dashboard.repo.OnboardingTaskRepository;
import org.springframework.stereotype.Service;

@Service
public class OnboardingService {

    private final OnboardingTaskRepository onboardingTaskRepository;

    public OnboardingService(OnboardingTaskRepository onboardingTaskRepository) {
        this.onboardingTaskRepository = onboardingTaskRepository;
    }

    public void createDefaultPipelineIfNeeded(Client client) {
        if (client == null || client.getId() == null) {
            return;
        }

        if (onboardingTaskRepository.existsByClient(client)) {
            return;
        }

        OnboardingTaskType[] steps = {
                OnboardingTaskType.BRIEFING_COMERCIAL,
                OnboardingTaskType.PRIMEIRO_CONTATO,
                OnboardingTaskType.FORMULARIO_PREENCHIDO,
                OnboardingTaskType.LINK_REUNIAO_ENVIADO,
                OnboardingTaskType.REUNIAO_ONBOARDING_AGENDADA,
                OnboardingTaskType.REUNIAO_FEITA,
                OnboardingTaskType.CONFIG_META,
                OnboardingTaskType.CONFIG_GOOGLE,
                OnboardingTaskType.CONFIG_BOT,
                OnboardingTaskType.CONFIG_CRM,
                OnboardingTaskType.PEDIDO_CRIATIVOS,
                OnboardingTaskType.SUBIR_PRIMEIROS_ANUNCIOS,
                OnboardingTaskType.VALIDACAO_CRIATIVOS,
                OnboardingTaskType.FASE_ESCALA
        };

        for (int i = 0; i < steps.length; i++) {
            OnboardingTask task = new OnboardingTask();
            task.setClient(client);
            task.setTaskType(steps[i]);
            task.setStatus(OnboardingTaskStatus.PENDING);
            task.setSortOrder(i + 1);

            onboardingTaskRepository.save(task);
        }
    }
}