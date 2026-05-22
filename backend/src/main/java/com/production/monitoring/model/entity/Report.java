package com.production.monitoring.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Metadato de reportes generados para auditoría o historial.
 */
@Getter
@Setter
@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reportType;
    private String generatedBy;
    private String fileReference;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
