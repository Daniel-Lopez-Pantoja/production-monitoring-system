package com.production.monitoring.model.entity;

import com.production.monitoring.model.enums.ServerModel;
import com.production.monitoring.model.enums.ServerStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Representa un servidor físico que pasa por instalación, pruebas, fallas y liberación.
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

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
