package com.production.monitoring.dto;

import java.util.Map;

/**
 * Respuesta operativa para las tarjetas, gráficas y tablas principales del dashboard.
 */
public record DashboardResponse(
        long totalServers,
        Map<String, Long> serversByStatus,
        long serversInTest,
        long failedServers,
        long releasedServers,
        long pendingOsServers,
        long debugServers,
        long retestServers,
        long criticalFailures,
        long openFailures,
        long closedFailures,
        Map<String, Long> failuresBySeverity,
        long totalTestsExecuted,
        long passedTests,
        long pendingTests,
        long failedTests,
        long retestTests,
        Map<String, Long> testsByResult,
        Map<String, Long> dailyTestThroughput,
        Object latestServers,
        Object latestFailures
) {}
