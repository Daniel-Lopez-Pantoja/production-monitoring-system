package com.production.monitoring.model.entity;

import com.production.monitoring.model.enums.FailureStatus;
import com.production.monitoring.model.enums.Severity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Falla encontrada durante una prueba, con severidad, estado y acción correctiva.
 */
@Getter
@Setter
@Entity
@Table(name = "failures")
public class Failure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private ProductionServer server;

    @ManyToOne(optional = false)
    private TestCatalog testCatalog;

    @Column(nullable = false, length = 1200)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FailureStatus status = FailureStatus.OPEN;

    @Column(length = 1200)
    private String correctiveAction;

    @ManyToOne
    private User assignedTechnician;

    private LocalDateTime detectedAt;
    private LocalDateTime closedAt;

    @Column(length = 1200)
    private String comments;

    private String evidenceLog;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
