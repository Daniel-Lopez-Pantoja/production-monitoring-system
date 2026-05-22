package com.production.monitoring.model.entity;

import com.production.monitoring.model.enums.TestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Registro de ejecución de una prueba específica para un servidor.
 */
@Getter
@Setter
@Entity
@Table(name = "server_tests")
public class ServerTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private ProductionServer server;

    @ManyToOne(optional = false)
    private TestCatalog testCatalog;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestStatus status = TestStatus.NOT_STARTED;

    private String result;

    @ManyToOne(optional = false)
    private User technician;

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    @Column(length = 1200)
    private String comments;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
