package com.production.monitoring.repository;

import com.production.monitoring.model.entity.ProductionServer;
import com.production.monitoring.model.enums.ServerModel;
import com.production.monitoring.model.enums.ServerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductionServerRepository extends JpaRepository<ProductionServer, Long> {
    boolean existsBySerialNumber(String serialNumber);
    boolean existsByInternalId(String internalId);
    Optional<ProductionServer> findBySerialNumber(String serialNumber);
    List<ProductionServer> findByStatus(ServerStatus status);
    List<ProductionServer> findByModel(ServerModel model);
    List<ProductionServer> findTop5ByOrderByUpdatedAtDesc();
}
