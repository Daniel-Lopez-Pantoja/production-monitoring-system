package com.production.monitoring.repository;

import com.production.monitoring.model.entity.TraceabilityRecord;
import com.production.monitoring.model.enums.Severity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TraceabilityRecordRepository extends JpaRepository<TraceabilityRecord, Long> {
    List<TraceabilityRecord> findByServerSerialNumberContainingIgnoreCase(String serial);
    List<TraceabilityRecord> findBySeverity(Severity severity);
}
