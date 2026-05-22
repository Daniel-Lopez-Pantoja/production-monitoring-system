package com.production.monitoring.controller;

import com.production.monitoring.model.entity.RaspberryDevice;
import com.production.monitoring.repository.RaspberryDeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints para administrar Raspberry devices usadas en automatización de pruebas.
 */
@RestController
@RequestMapping("/api/raspberries")
@RequiredArgsConstructor
public class RaspberryController {
    private final RaspberryDeviceRepository repository;

    @GetMapping
    public List<RaspberryDevice> findAll() {
        return repository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public RaspberryDevice create(@RequestBody RaspberryDevice device) {
        return repository.save(device);
    }
}
