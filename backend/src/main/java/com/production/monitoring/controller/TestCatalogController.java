package com.production.monitoring.controller;

import com.production.monitoring.model.entity.TestCatalog;
import com.production.monitoring.repository.TestCatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints para consultar el catálogo maestro de pruebas.
 */
@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestCatalogController {
    private final TestCatalogRepository repository;

    @GetMapping
    public List<TestCatalog> findAll() {
        return repository.findAll();
    }
}
