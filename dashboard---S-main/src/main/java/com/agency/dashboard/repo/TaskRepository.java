package com.agency.dashboard.repo;

import com.agency.dashboard.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}