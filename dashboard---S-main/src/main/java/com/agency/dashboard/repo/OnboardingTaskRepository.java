package com.agency.dashboard.repo;

import com.agency.dashboard.domain.Client;
import com.agency.dashboard.domain.OnboardingTask;
import com.agency.dashboard.domain.OnboardingTaskStatus;
import com.agency.dashboard.domain.OnboardingTaskType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OnboardingTaskRepository extends JpaRepository<OnboardingTask, Long> {

    List<OnboardingTask> findByClientOrderBySortOrderAsc(Client client);

    boolean existsByClient(Client client);

    List<OnboardingTask> findAllByOrderByCreatedAtDesc();

    List<OnboardingTask> findByStatusOrderBySortOrderAsc(OnboardingTaskStatus status);

    List<OnboardingTask> findByTaskTypeOrderBySortOrderAsc(OnboardingTaskType taskType);

    List<OnboardingTask> findByClientAndStatusOrderBySortOrderAsc(Client client, OnboardingTaskStatus status);

    List<OnboardingTask> findByClientAndTaskTypeOrderBySortOrderAsc(Client client, OnboardingTaskType taskType);

    List<OnboardingTask> findByStatusAndTaskTypeOrderBySortOrderAsc(OnboardingTaskStatus status, OnboardingTaskType taskType);

    List<OnboardingTask> findByClientAndStatusAndTaskTypeOrderBySortOrderAsc(
            Client client,
            OnboardingTaskStatus status,
            OnboardingTaskType taskType
    );
}