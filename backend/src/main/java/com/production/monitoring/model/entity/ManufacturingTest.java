package com.production.monitoring.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.production.monitoring.model.enums.ManufacturingTestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "manufacturing_tests")
public class ManufacturingTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JsonIgnore
    private ProductionServer server;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer sequenceOrder;

    @Column(length = 1600)
    private String description;

    @Column(nullable = false)
    private Integer estimatedMinMinutes;

    @Column(nullable = false)
    private Integer estimatedMaxMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ManufacturingTestStatus status = ManufacturingTestStatus.PENDING;

    private String failureCategory;

    @ElementCollection
    @CollectionTable(name = "manufacturing_test_possible_failures", joinColumns = @JoinColumn(name = "manufacturing_test_id"))
    @Column(name = "possible_failure", length = 600)
    private List<String> possibleFailures = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "manufacturing_test_recommended_actions", joinColumns = @JoinColumn(name = "manufacturing_test_id"))
    @Column(name = "recommended_action", length = 600)
    private List<String> recommendedActions = new ArrayList<>();

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime completedAt;
    private LocalDateTime failedAt;

    @Column(length = 1200)
    private String observations;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
