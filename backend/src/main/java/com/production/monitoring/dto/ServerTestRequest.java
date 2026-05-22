package com.production.monitoring.dto;

import com.production.monitoring.model.enums.TestStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Entrada para registrar o actualizar el resultado de una prueba ejecutada en un servidor.
 */
public record ServerTestRequest(
        @NotNull Long serverId,
        @NotNull Long testCatalogId,
        @NotNull TestStatus status,
        String result,
        @NotNull Long technicianId,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        String comments
) {}
