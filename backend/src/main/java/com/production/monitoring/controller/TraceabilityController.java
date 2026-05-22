package com.production.monitoring.controller;

import com.production.monitoring.dto.TraceabilityRequest;
import com.production.monitoring.model.entity.TraceabilityRecord;
import com.production.monitoring.service.TraceabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints para consultar y alimentar la matriz de trazabilidad.
 */
@RestController
@RequestMapping("/api/traceability")
@RequiredArgsConstructor
public class TraceabilityController {
    private final TraceabilityService traceabilityService;

    @GetMapping
    public List<TraceabilityRecord> findAll() {
        return traceabilityService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER','TECHNICIAN')")
    public TraceabilityRecord create(@Valid @RequestBody TraceabilityRequest request) {
        return traceabilityService.create(request);
    }
}
