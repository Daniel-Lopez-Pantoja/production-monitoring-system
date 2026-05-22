package com.production.monitoring.controller;

import com.production.monitoring.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Endpoints de reportes agregados para tablas, gráficas y exportación CSV.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/servers-by-status")
    public Map<String, Long> serversByStatus() {
        return reportService.serversByStatus();
    }

    @GetMapping("/failures-by-test")
    public Map<String, Long> failuresByTest() {
        return reportService.failuresByTest();
    }

    @GetMapping("/failures-by-model")
    public Map<String, Long> failuresByModel() {
        return reportService.failuresByModel();
    }
}
