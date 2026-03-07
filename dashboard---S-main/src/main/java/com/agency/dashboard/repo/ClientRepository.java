package com.agency.dashboard.repo;

import com.agency.dashboard.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByNameIgnoreCase(String name);

}