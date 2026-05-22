package com.production.monitoring.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Acción correctiva aplicada a una falla para dejar evidencia de reparación.
 */
@Getter
@Setter
@Entity
@Table(name = "corrective_actions")
public class CorrectiveAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Failure failure;

    @ManyToOne(optional = false)
    private User performedBy;

    @Column(nullable = false, length = 1200)
    private String actionDescription;

    private String evidence;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
