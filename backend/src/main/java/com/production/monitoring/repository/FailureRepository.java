package com.production.monitoring.repository;

import com.production.monitoring.model.entity.Failure;
import com.production.monitoring.model.enums.FailureStatus;
import com.production.monitoring.model.enums.Severity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FailureRepository extends JpaRepository<Failure, Long> {
    long countBySeverity(Severity severity);
    long countByStatusNot(FailureStatus status);
    boolean existsByServerIdAndStatusNot(Long serverId, FailureStatus status);
    List<Failure> findTop5ByOrderByDetectedAtDesc();
    List<Failure> findBySeverity(Severity severity);
}
