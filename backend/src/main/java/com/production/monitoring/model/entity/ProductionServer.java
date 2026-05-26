package com.production.monitoring.model.entity;

import com.production.monitoring.model.enums.ServerModel;
import com.production.monitoring.model.enums.ServerStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.production.monitoring.model.enums.ManufacturingTestStatus;

/**
 * Represents a physical server that moves through validation, failure handling, and release.
 */
@Getter
@Setter
@Entity
@Table(name = "servers")
public class ProductionServer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String internalId;

    @Column(nullable = false, unique = true)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServerModel model;

    private String rackNumber;
    private String location;

    @Min(1)
    @Max(32)
    private Integer nicheNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServerStatus status = ServerStatus.PENDING_OS;

    private LocalDateTime entryDate;

    @ManyToOne
    private User responsibleEngineer;

    @ManyToOne
    private User assignedTechnician;

    @Column(length = 1200)
    private String observations;

    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("sequenceOrder ASC")
    private List<ManufacturingTest> manufacturingTests = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Transient
    public int getServerProgressPercentage() {
        if (manufacturingTests == null || manufacturingTests.isEmpty()) return 0;
        long completedTests = manufacturingTests.stream()
                .filter(test -> test.getStatus() == ManufacturingTestStatus.PASSED || test.getStatus() == ManufacturingTestStatus.SKIPPED)
                .count();
        return (int) Math.round((completedTests * 100.0) / manufacturingTests.size());
    }

    @Transient
    public String getCurrentRunningTest() {
        ManufacturingTest runningTest = findFirstByStatus(ManufacturingTestStatus.IN_PROGRESS);
        if (runningTest != null) return runningTest.getName();

        ManufacturingTest failedTest = findFirstByStatus(ManufacturingTestStatus.FAILED);
        if (failedTest != null) return "Failed at " + failedTest.getName();

        boolean hasPendingTests = manufacturingTests != null && manufacturingTests.stream()
                .anyMatch(test -> test.getStatus() == ManufacturingTestStatus.PENDING);
        if (!hasPendingTests && manufacturingTests != null && !manufacturingTests.isEmpty()) return "Completed";
        return "Pending Start";
    }

    @Transient
    public int getEstimatedRemainingMinutes() {
        if (manufacturingTests == null || manufacturingTests.isEmpty()) return 0;
        if (findFirstByStatus(ManufacturingTestStatus.FAILED) != null) return 0;

        return manufacturingTests.stream()
                .filter(test -> test.getStatus() == ManufacturingTestStatus.PENDING || test.getStatus() == ManufacturingTestStatus.IN_PROGRESS)
                .mapToInt(this::remainingMinutesFor)
                .sum();
    }

    @Transient
    public String getEstimatedRemainingTime() {
        int minutes = getEstimatedRemainingMinutes();
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;
        return hours + "h " + remainingMinutes + "m";
    }

    private ManufacturingTest findFirstByStatus(ManufacturingTestStatus status) {
        if (manufacturingTests == null) return null;
        return manufacturingTests.stream()
                .filter(test -> test.getStatus() == status)
                .min(Comparator.comparing(ManufacturingTest::getSequenceOrder))
                .orElse(null);
    }

    private int remainingMinutesFor(ManufacturingTest test) {
        if (test.getStatus() == ManufacturingTestStatus.PENDING || test.getStartTime() == null) {
            return safeMaxMinutes(test);
        }
        long elapsedMinutes = Duration.between(test.getStartTime(), LocalDateTime.now()).toMinutes();
        return Math.max(0, safeMaxMinutes(test) - (int) elapsedMinutes);
    }

    private int safeMaxMinutes(ManufacturingTest test) {
        return test.getEstimatedMaxMinutes() == null ? 0 : test.getEstimatedMaxMinutes();
    }
}
