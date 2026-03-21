package com.agency.dashboard.repo;

import com.agency.dashboard.domain.TrafficAd;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrafficAdRepository extends JpaRepository<TrafficAd, Long> {
}