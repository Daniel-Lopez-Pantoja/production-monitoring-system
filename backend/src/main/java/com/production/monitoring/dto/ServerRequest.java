package com.production.monitoring.dto;

import com.production.monitoring.model.enums.ServerModel;
import com.production.monitoring.model.enums.ServerStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Request payload used to create or update a server from the API.
 */
public record ServerRequest(
        @NotBlank String internalId,
        @NotBlank String serialNumber,
        @NotNull ServerModel model,
        String rackNumber,
        String location,
        @NotNull @Min(1) @Max(32) Integer nicheNumber,
        @NotNull ServerStatus status,
        LocalDateTime entryDate,
        Long responsibleEngineerId,
        Long assignedTechnicianId,
        String observations
) {}
