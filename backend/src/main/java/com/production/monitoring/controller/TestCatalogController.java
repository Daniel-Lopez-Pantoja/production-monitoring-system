package com.production.monitoring.controller;

import com.production.monitoring.model.entity.TestCatalog;
import com.production.monitoring.service.TestCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestCatalogController {
    private final TestCatalogService testCatalogService;

    @GetMapping
    public List<TestCatalog> findAll() {
        return testCatalogService.findAll();
    }
}
