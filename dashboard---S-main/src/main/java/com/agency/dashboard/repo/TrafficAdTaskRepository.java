package com.agency.dashboard.repo;

import com.agency.dashboard.domain.Client;
import com.agency.dashboard.domain.TrafficAdStatus;
import com.agency.dashboard.domain.TrafficAdTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrafficAdTaskRepository extends JpaRepository<TrafficAdTask, Long> {

    List<TrafficAdTask> findAllByOrderByCreatedAtDesc();

    List<TrafficAdTask> findByClientOrderByCreatedAtDesc(Client client);

    List<TrafficAdTask> findByStatusOrderByCreatedAtDesc(TrafficAdStatus status);

    List<TrafficAdTask> findByClientAndStatusOrderByCreatedAtDesc(Client client, TrafficAdStatus status);
}