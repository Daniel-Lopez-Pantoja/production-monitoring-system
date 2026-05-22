package com.production.monitoring.service;

import com.production.monitoring.dto.DashboardResponse;
import com.production.monitoring.model.enums.ServerStatus;
import com.production.monitoring.model.enums.Severity;
import com.production.monitoring.model.enums.TestStatus;
import com.production.monitoring.repository.FailureRepository;
import com.production.monitoring.repository.ProductionServerRepository;
import com.production.monitoring.repository.ServerTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Calcula métricas operativas para el dashboard principal.
 */
@Service
@RequiredArgsConstructor
public class DashboardService {
    private final ProductionServerRepository serverRepository;
    private final FailureRepository failureRepository;
    private final ServerTestRepository serverTestRepository;

    public DashboardResponse getDashboard() {
        Map<String, Long> byStatus = Arrays.stream(ServerStatus.values())
                .collect(Collectors.toMap(Enum::name, status -> (long) serverRepository.findByStatus(status).size()));
        return new DashboardResponse(
                serverRepository.count(),
                byStatus,
                byStatus.get(ServerStatus.IN_TEST.name()),
                byStatus.get(ServerStatus.FAILED.name()),
                byStatus.get(ServerStatus.RELEASED.name()),
                failureRepository.countBySeverity(Severity.CRITICAL),
                serverTestRepository.countByStatus(TestStatus.NOT_STARTED),
                serverTestRepository.countByStatus(TestStatus.FAILED),
                serverRepository.findTop5ByOrderByUpdatedAtDesc(),
                failureRepository.findTop5ByOrderByDetectedAtDesc()
        );
    }
}
