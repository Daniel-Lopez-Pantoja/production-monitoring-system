package com.production.monitoring.dto;

import java.util.Map;

/**
 * Respuesta compacta para las tarjetas y tablas principales del dashboard.
 */
public record DashboardResponse(
        long totalServers,
        Map<String, Long> serversByStatus,
        long serversInTest,
        long failedServers,
        long releasedServers,
        long criticalFailures,
        long pendingTests,
        long failedTests,
        Object latestServers,
        Object latestFailures
) {}
