package com.agency.dashboard.repo;

import com.agency.dashboard.domain.Client;
import com.agency.dashboard.domain.Task;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Modifying
    @Transactional
    void deleteByClient(Client client);
}