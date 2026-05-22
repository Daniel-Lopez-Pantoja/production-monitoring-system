package com.production.monitoring.controller;

import com.production.monitoring.dto.ServerTestRequest;
import com.production.monitoring.model.entity.ServerTest;
import com.production.monitoring.service.ServerTestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints para registrar resultados de pruebas por servidor.
 */
@RestController
@RequestMapping("/api/server-tests")
@RequiredArgsConstructor
public class ServerTestController {
    private final ServerTestService serverTestService;

    @GetMapping
    public List<ServerTest> findAll() {
        return serverTestService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER','TECHNICIAN')")
    public ServerTest create(@Valid @RequestBody ServerTestRequest request) {
        return serverTestService.create(request);
    }
}
