package com.agency.dashboard.service;

import com.agency.dashboard.domain.Client;
import com.agency.dashboard.domain.OnboardingTask;
import com.agency.dashboard.domain.OnboardingTaskStatus;
import com.agency.dashboard.domain.OnboardingTaskType;
import com.agency.dashboard.repo.OnboardingTaskRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OnboardingService {

    private final OnboardingTaskRepository onboardingTaskRepository;
    private final NotificationService notificationService;

    public OnboardingService(
            OnboardingTaskRepository onboardingTaskRepository,
            NotificationService notificationService
    ) {
        this.onboardingTaskRepository = onboardingTaskRepository;
        this.notificationService = notificationService;
    }

    public void createDefaultPipelineIfNeeded(Client client) {
        if (onboardingTaskRepository.existsByClient(client)) {
            return;
        }

        List<OnboardingTask> tasks = new ArrayList<>();
        int sortOrder = 1;

        tasks.add(createTask(client, OnboardingTaskType.BRIEFING_COMERCIAL, sortOrder++));
        tasks.add(createTask(client, OnboardingTaskType.PRIMEIRO_CONTATO, sortOrder++));
        tasks.add(createTask(client, OnboardingTaskType.FORMULARIO_PREENCHIDO, sortOrder++));
        tasks.add(createTask(client, OnboardingTaskType.LINK_REUNIAO_ENVIADO, sortOrder++));
        tasks.add(createTask(client, OnboardingTaskType.REUNIAO_FEITA, sortOrder++));
        tasks.add(createTask(client, OnboardingTaskType.CONFIG_META, sortOrder++));
        tasks.add(createTask(client, OnboardingTaskType.CONFIG_GOOGLE, sortOrder++));
        tasks.add(createTask(client, OnboardingTaskType.CONFIG_BOT, sortOrder++));
        tasks.add(createTask(client, OnboardingTaskType.CONFIG_CRM, sortOrder++));
        tasks.add(createTask(client, OnboardingTaskType.PEDIDO_CRIATIVOS, sortOrder++));

        onboardingTaskRepository.saveAll(tasks);

        notificationService.createNotification(
                "Novo onboarding gerado",
                "O cliente " + client.getName() + " teve o pipeline de onboarding criado para o tráfego.",
                "TRAFEGO",
                null,
                "CLIENT",
                client.getId()
        );
    }

    private OnboardingTask createTask(Client client, OnboardingTaskType taskType, int sortOrder) {
        OnboardingTask task = new OnboardingTask();
        task.setClient(client);
        task.setTaskType(taskType);
        task.setStatus(OnboardingTaskStatus.PENDING);
        task.setSortOrder(sortOrder);
        return task;
    }
}