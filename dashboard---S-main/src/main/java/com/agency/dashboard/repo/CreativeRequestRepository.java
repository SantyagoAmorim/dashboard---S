package com.agency.dashboard.repo;

import com.agency.dashboard.domain.Client;
import com.agency.dashboard.domain.CreativeRequest;
import com.agency.dashboard.domain.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CreativeRequestRepository extends JpaRepository<CreativeRequest, Long> {

    List<CreativeRequest> findTop50ByOrderByCreatedAtDesc();

    List<CreativeRequest> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByStatusNotAndCreatedAtBetween(RequestStatus status, LocalDateTime start, LocalDateTime end);

    long countByStatusAndDeliveredAtBetween(RequestStatus status, LocalDateTime start, LocalDateTime end);

    List<CreativeRequest> findByClientAndCreatedAtBetween(Client client, LocalDateTime start, LocalDateTime end);

}