package com.production.monitoring.dto;

import com.production.monitoring.model.enums.ServerModel;
import com.production.monitoring.model.enums.ServerStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Datos necesarios para crear o actualizar un servidor desde la API.
 */
public record ServerRequest(
        @NotBlank String internalId,
        @NotBlank String serialNumber,
        @NotNull ServerModel model,
        String rackNumber,
        String location,
        @NotNull ServerStatus status,
        LocalDateTime entryDate,
        Long responsibleEngineerId,
        Long assignedTechnicianId,
        String observations
) {}
