package com.production.monitoring.controller;

import com.production.monitoring.dto.ServerRequest;
import com.production.monitoring.model.entity.ProductionServer;
import com.production.monitoring.service.ServerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints para administrar servidores de producción y pruebas.
 */
@RestController
@RequestMapping("/api/servers")
@RequiredArgsConstructor
public class ServerController {
    private final ServerService serverService;

    @GetMapping
    public List<ProductionServer> findAll() {
        return serverService.findAll();
    }

    @GetMapping("/{id}")
    public ProductionServer findById(@PathVariable Long id) {
        return serverService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public ProductionServer create(@Valid @RequestBody ServerRequest request) {
        return serverService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER','TECHNICIAN')")
    public ProductionServer update(@PathVariable Long id, @Valid @RequestBody ServerRequest request) {
        return serverService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        serverService.delete(id);
    }
}
