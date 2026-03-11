package com.agency.dashboard.repo;

import com.agency.dashboard.domain.Lead;
import com.agency.dashboard.domain.LeadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeadRepository extends JpaRepository<Lead, Long> {

    List<Lead> findByStatusOrderByCreatedAtDesc(LeadStatus status);

}