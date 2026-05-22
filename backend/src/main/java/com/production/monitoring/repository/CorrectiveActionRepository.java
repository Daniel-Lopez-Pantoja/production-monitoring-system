package com.production.monitoring.repository;

import com.production.monitoring.model.entity.CorrectiveAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorrectiveActionRepository extends JpaRepository<CorrectiveAction, Long> {
}
