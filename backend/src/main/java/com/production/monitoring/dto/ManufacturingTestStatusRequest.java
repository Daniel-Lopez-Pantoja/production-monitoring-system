package com.production.monitoring.dto;

import com.production.monitoring.model.enums.ManufacturingTestStatus;
import jakarta.validation.constraints.NotNull;

public record ManufacturingTestStatusRequest(
        @NotNull ManufacturingTestStatus status,
        String observations
) {}
