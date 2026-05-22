package com.production.monitoring.service;

import com.production.monitoring.dto.DashboardResponse;
import com.production.monitoring.model.enums.FailureStatus;
import com.production.monitoring.model.enums.ServerStatus;
import com.production.monitoring.model.enums.Severity;
import com.production.monitoring.model.enums.TestStatus;
import com.production.monitoring.repository.FailureRepository;
import com.production.monitoring.repository.ProductionServerRepository;
import com.production.monitoring.repository.ServerTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashMap;
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
        Map<String, Long> failuresBySeverity = Arrays.stream(Severity.values())
                .collect(Collectors.toMap(Enum::name, severity -> failureRepository.countBySeverity(severity)));
        Map<String, Long> testsByResult = Arrays.stream(TestStatus.values())
                .collect(Collectors.toMap(Enum::name, status -> serverTestRepository.countByStatus(status)));
        Map<String, Long> dailyThroughput = dailyTestThroughput();
        return new DashboardResponse(
                serverRepository.count(),
                byStatus,
                byStatus.get(ServerStatus.IN_TEST.name()),
                byStatus.get(ServerStatus.FAILED.name()),
                byStatus.get(ServerStatus.RELEASED.name()),
                byStatus.get(ServerStatus.PENDING_OS.name()),
                byStatus.get(ServerStatus.DEBUG.name()),
                byStatus.get(ServerStatus.RETEST.name()),
                failureRepository.countBySeverity(Severity.CRITICAL),
                failureRepository.countByStatusNot(FailureStatus.CLOSED),
                failureRepository.countByStatus(FailureStatus.CLOSED),
                failuresBySeverity,
                serverTestRepository.count(),
                serverTestRepository.countByStatus(TestStatus.PASSED),
                serverTestRepository.countByStatus(TestStatus.NOT_STARTED),
                serverTestRepository.countByStatus(TestStatus.FAILED),
                serverTestRepository.countByStatus(TestStatus.RETEST),
                testsByResult,
                dailyThroughput,
                serverRepository.findTop5ByOrderByUpdatedAtDesc(),
                failureRepository.findTop5ByOrderByDetectedAtDesc()
        );
    }

    /**
     * Agrupa las pruebas finalizadas de los últimos 7 días para alimentar la gráfica de throughput diario.
     */
    private Map<String, Long> dailyTestThroughput() {
        Map<String, Long> throughput = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        for (int daysBack = 6; daysBack >= 0; daysBack--) {
            LocalDate day = today.minusDays(daysBack);
            long total = serverTestRepository.findAll().stream()
                    .filter(test -> test.getFinishedAt() != null)
                    .filter(test -> test.getFinishedAt().toLocalDate().equals(day))
                    .count();
            throughput.put(day.getMonthValue() + "/" + day.getDayOfMonth(), total);
        }
        return throughput;
    }
}
