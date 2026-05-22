package com.production.monitoring.dto;

import com.production.monitoring.model.enums.FailureStatus;
import com.production.monitoring.model.enums.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Entrada para registrar o actualizar una falla detectada.
 */
public record FailureRequest(
        @NotNull Long serverId,
        @NotNull Long testCatalogId,
        @NotBlank String description,
        @NotNull Severity severity,
        @NotNull FailureStatus status,
        String correctiveAction,
        Long assignedTechnicianId,
        LocalDateTime detectedAt,
        LocalDateTime closedAt,
        String comments,
        String evidenceLog
) {}
