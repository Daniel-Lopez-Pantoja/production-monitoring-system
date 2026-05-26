package com.production.monitoring.repository;

import com.production.monitoring.model.entity.ManufacturingTest;
import com.production.monitoring.model.enums.ManufacturingTestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ManufacturingTestRepository extends JpaRepository<ManufacturingTest, Long> {
    List<ManufacturingTest> findByServerIdOrderBySequenceOrderAsc(Long serverId);
    Optional<ManufacturingTest> findFirstByServerIdAndStatusOrderBySequenceOrderAsc(Long serverId, ManufacturingTestStatus status);
    long countByStatus(ManufacturingTestStatus status);
    boolean existsByServerIdAndStatus(Long serverId, ManufacturingTestStatus status);
}
