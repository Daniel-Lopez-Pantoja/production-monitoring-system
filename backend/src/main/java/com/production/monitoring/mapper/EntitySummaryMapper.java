package com.production.monitoring.mapper;

import com.production.monitoring.model.entity.Failure;
import com.production.monitoring.model.entity.ProductionServer;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Mapper simple para convertir entidades grandes en respuestas resumidas cuando sea necesario.
 */
@Component
public class EntitySummaryMapper {
    public Map<String, Object> serverSummary(ProductionServer server) {
        return Map.of(
                "id", server.getId(),
                "serialNumber", server.getSerialNumber(),
                "model", server.getModel(),
                "status", server.getStatus()
        );
    }

    public Map<String, Object> failureSummary(Failure failure) {
        return Map.of(
                "id", failure.getId(),
                "serialNumber", failure.getServer().getSerialNumber(),
                "test", failure.getTestCatalog().getName(),
                "severity", failure.getSeverity(),
                "status", failure.getStatus()
        );
    }
}
