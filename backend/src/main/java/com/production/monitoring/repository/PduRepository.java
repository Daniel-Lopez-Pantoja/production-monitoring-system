package com.production.monitoring.repository;

import com.production.monitoring.model.entity.Pdu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PduRepository extends JpaRepository<Pdu, Long> {
    boolean existsByName(String name);
}
