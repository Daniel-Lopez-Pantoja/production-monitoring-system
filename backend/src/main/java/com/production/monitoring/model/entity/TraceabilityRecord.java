package com.production.monitoring.model.entity;

import com.production.monitoring.model.enums.Severity;
import com.production.monitoring.model.enums.TestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Matriz de trazabilidad. Guarda el historial técnico completo por serial y prueba.
 */
@Getter
@Setter
@Entity
@Table(name = "traceability_records")
public class TraceabilityRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private ProductionServer server;

    @ManyToOne
    private RaspberryDevice raspberry;

    @ManyToOne
    private Pdu pdu;

    private String pduPort;
    private String coldRoom;
    private String physicalLocation;

    @ManyToOne(optional = false)
    private TestCatalog testCatalog;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestStatus testStatus = TestStatus.NOT_STARTED;

    private String result;
    private String detectedFailure;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Column(length = 1200)
    private String correctiveAction;

    @ManyToOne
    private User responsibleEngineer;

    @ManyToOne
    private User responsibleTechnician;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long durationMinutes;

    @Column(length = 1200)
    private String comments;

    private String evidenceLogReference;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Calcula la duración cuando existen fecha de inicio y fecha de fin.
     */
    @PrePersist
    @PreUpdate
    public void calculateDuration() {
        if (startDate != null && endDate != null) {
            durationMinutes = Duration.between(startDate, endDate).toMinutes();
        }
    }
}
