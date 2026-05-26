package com.production.monitoring.service;

import com.production.monitoring.dto.ManufacturingTestStatusRequest;
import com.production.monitoring.exception.BusinessRuleException;
import com.production.monitoring.exception.ResourceNotFoundException;
import com.production.monitoring.model.entity.ManufacturingTest;
import com.production.monitoring.model.entity.ProductionServer;
import com.production.monitoring.model.enums.ManufacturingTestStatus;
import com.production.monitoring.repository.ManufacturingTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManufacturingTestService {
    private final ManufacturingTestRepository manufacturingTestRepository;
    private final ValidationDurationService validationDurationService;

    public List<ManufacturingTest> findByServer(Long serverId) {
        return manufacturingTestRepository.findByServerIdOrderBySequenceOrderAsc(serverId);
    }

    public ManufacturingTest updateStatus(Long id, ManufacturingTestStatusRequest request) {
        ManufacturingTest test = manufacturingTestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manufacturing test not found."));
        applyStatus(test, request.status());
        test.setObservations(request.observations());
        return manufacturingTestRepository.save(test);
    }

    public void ensurePipeline(ProductionServer server) {
        if (server.getManufacturingTests() != null && !server.getManufacturingTests().isEmpty()) return;
        manufacturingPipelineTemplates().forEach(template -> {
            ManufacturingTest test = new ManufacturingTest();
            test.setServer(server);
            test.setName(template.name());
            test.setSequenceOrder(template.sequenceOrder());
            test.setDescription(template.description());
            test.setEstimatedMinMinutes(template.estimatedMinMinutes());
            test.setEstimatedMaxMinutes(template.estimatedMaxMinutes());
            test.setFailureCategory(template.failureCategory());
            test.setPossibleFailures(new ArrayList<>(template.possibleFailures()));
            test.setRecommendedActions(new ArrayList<>(template.recommendedActions()));
            server.getManufacturingTests().add(test);
        });
    }

    public List<ManufacturingTestTemplate> manufacturingPipelineTemplates() {
        return List.of(
                template(1, "Operating System Installation", "Installs and validates the base operating system image.", "OS_LOAD",
                        List.of("Boot media not detected", "PXE or USB boot failure", "Corrupted installation image"),
                        List.of("Verify boot order", "Validate install media", "Re-run OS deployment")),
                template(2, "Power-On Validation", "Confirms stable power-on, POST behavior, and initial platform readiness.", "POWER_ON",
                        List.of("No POST", "Unexpected reboot", "BIOS initialization failure"),
                        List.of("Check power path", "Review POST codes", "Validate BIOS settings")),
                template(3, "LED Functional Verification", "Validates power, UID, NIC, fan, PSU, and fault indicator LEDs.", "VISUAL_INDICATORS",
                        List.of("LED does not turn on", "Incorrect LED state", "Intermittent indicator behavior"),
                        List.of("Verify LED harness", "Validate firmware state", "Re-run LED command sequence")),
                template(4, "Ethernet Connectivity Validation", "Validates physical link, negotiated speed, and basic network reachability.", "NETWORK",
                        List.of("Ethernet cable disconnected", "NIC failure", "Incorrect switch port", "Driver issue"),
                        List.of("Verify physical cable", "Validate NIC LEDs", "Reconnect cable", "Re-run network validation")),
                template(5, "Network Traffic Validation", "Runs network traffic checks for packet loss, throughput, and link stability.", "NETWORK_TRAFFIC",
                        List.of("Packet loss", "Low throughput", "Unstable link negotiation"),
                        List.of("Check switch configuration", "Replace cable", "Run throughput validation again")),
                template(6, "Thermal Stability Test", "Validates thermal behavior under controlled workload before burn-in.", "THERMAL",
                        List.of("Thermal throttling", "Fan curve mismatch", "Temperature sensor anomaly"),
                        List.of("Inspect heatsink seating", "Validate fan profile", "Review sensor telemetry")),
                template(7, "Burn-In Validation", "Runs prolonged workload to detect early-life failures before release.", "BURN_IN",
                        List.of("Unexpected reboot", "Thermal shutdown", "Intermittent workload failure"),
                        List.of("Review system logs", "Repeat burn-in cycle", "Escalate to debug station")),
                template(8, "Storage Validation", "Validates disk detection, SMART health, RAID state, and read/write behavior.", "STORAGE",
                        List.of("NVMe drive not detected", "SMART error", "Low storage throughput"),
                        List.of("Reseat drive", "Check backplane or cable", "Run storage diagnostics")),
                template(9, "Memory Validation", "Runs DIMM stability and ECC validation under memory-intensive workload.", "MEMORY",
                        List.of("DIMM not detected", "ECC correctable errors", "Uncorrectable memory error"),
                        List.of("Reseat DIMM", "Run memory diagnostics", "Replace failing memory module")),
                template(10, "Final Functional Validation", "Performs final readiness checks before server release.", "FINAL_VALIDATION",
                        List.of("Inventory mismatch", "Open validation failure", "Missing release evidence"),
                        List.of("Review validation checklist", "Confirm all tests passed", "Approve release handoff"))
        );
    }

    private ManufacturingTestTemplate template(int sequenceOrder, String name, String description, String failureCategory,
                                               List<String> possibleFailures, List<String> recommendedActions) {
        ValidationDurationService.DurationRange duration = validationDurationService.durationForTestName(name);
        return new ManufacturingTestTemplate(sequenceOrder, name, description, duration.estimatedMinMinutes(),
                duration.estimatedMaxMinutes(), failureCategory, possibleFailures, recommendedActions);
    }

    private void applyStatus(ManufacturingTest test, ManufacturingTestStatus status) {
        if (status == ManufacturingTestStatus.IN_PROGRESS) {
            validatePreviousTestsPassed(test);
            test.setStartTime(test.getStartTime() == null ? LocalDateTime.now() : test.getStartTime());
        }
        if (status == ManufacturingTestStatus.PASSED || status == ManufacturingTestStatus.SKIPPED) {
            test.setCompletedAt(LocalDateTime.now());
            test.setEndTime(LocalDateTime.now());
        }
        if (status == ManufacturingTestStatus.FAILED) {
            test.setFailedAt(LocalDateTime.now());
            test.setEndTime(LocalDateTime.now());
        }
        test.setStatus(status);
    }

    private void validatePreviousTestsPassed(ManufacturingTest test) {
        boolean hasUnfinishedPreviousTest = manufacturingTestRepository.findByServerIdOrderBySequenceOrderAsc(test.getServer().getId()).stream()
                .filter(previous -> previous.getSequenceOrder() < test.getSequenceOrder())
                .anyMatch(previous -> previous.getStatus() != ManufacturingTestStatus.PASSED && previous.getStatus() != ManufacturingTestStatus.SKIPPED);
        if (hasUnfinishedPreviousTest) {
            throw new BusinessRuleException("Previous manufacturing tests must pass before starting this test.");
        }
    }

    public record ManufacturingTestTemplate(
            int sequenceOrder,
            String name,
            String description,
            int estimatedMinMinutes,
            int estimatedMaxMinutes,
            String failureCategory,
            List<String> possibleFailures,
            List<String> recommendedActions
    ) {}
}
