package com.production.monitoring.service;

import com.production.monitoring.repository.FailureRepository;
import com.production.monitoring.repository.ProductionServerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Genera reportes simples para análisis y exportación del frontend.
 */
@Service
@RequiredArgsConstructor
public class ReportService {
    private final ProductionServerRepository serverRepository;
    private final FailureRepository failureRepository;

    public Map<String, Long> serversByStatus() {
        return serverRepository.findAll().stream()
                .collect(Collectors.groupingBy(server -> server.getStatus().name(), Collectors.counting()));
    }

    public Map<String, Long> failuresByTest() {
        return failureRepository.findAll().stream()
                .collect(Collectors.groupingBy(failure -> failure.getTestCatalog().getName(), Collectors.counting()));
    }

    public Map<String, Long> failuresByModel() {
        return failureRepository.findAll().stream()
                .collect(Collectors.groupingBy(failure -> failure.getServer().getModel().name(), Collectors.counting()));
    }
}
