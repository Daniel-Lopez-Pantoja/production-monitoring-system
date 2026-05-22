package com.production.monitoring.controller;

import com.production.monitoring.dto.FailureRequest;
import com.production.monitoring.model.entity.Failure;
import com.production.monitoring.service.FailureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints para registrar y consultar fallas.
 */
@RestController
@RequestMapping("/api/failures")
@RequiredArgsConstructor
public class FailureController {
    private final FailureService failureService;

    @GetMapping
    public List<Failure> findAll() {
        return failureService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public Failure create(@Valid @RequestBody FailureRequest request) {
        return failureService.create(request);
    }
}
