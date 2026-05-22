package com.production.monitoring.controller;

import com.production.monitoring.model.entity.Pdu;
import com.production.monitoring.repository.PduRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints para administrar PDUs usadas en racks de prueba.
 */
@RestController
@RequestMapping("/api/pdus")
@RequiredArgsConstructor
public class PduController {
    private final PduRepository repository;

    @GetMapping
    public List<Pdu> findAll() {
        return repository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public Pdu create(@RequestBody Pdu pdu) {
        return repository.save(pdu);
    }
}
