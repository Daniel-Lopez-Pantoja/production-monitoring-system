package com.production.monitoring.dto;

import com.production.monitoring.model.enums.Severity;
import com.production.monitoring.model.enums.TestStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Entrada para registrar una línea de trazabilidad de servidor y prueba.
 */
public record TraceabilityRequest(
        @NotNull Long serverId,
        Long raspberryId,
        Long pduId,
        String pduPort,
        String coldRoom,
        String physicalLocation,
        @NotNull Long testCatalogId,
        @NotNull TestStatus testStatus,
        String result,
        String detectedFailure,
        Severity severity,
        String correctiveAction,
        Long responsibleEngineerId,
        @NotNull Long responsibleTechnicianId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String comments,
        String evidenceLogReference
) {}
