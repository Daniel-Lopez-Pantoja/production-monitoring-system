package com.production.monitoring.repository;

import com.production.monitoring.model.entity.ServerTest;
import com.production.monitoring.model.enums.TestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerTestRepository extends JpaRepository<ServerTest, Long> {
    long countByStatus(TestStatus status);
    boolean existsByServerIdAndTestCatalogCriticalTrueAndStatus(Long serverId, TestStatus status);
}
