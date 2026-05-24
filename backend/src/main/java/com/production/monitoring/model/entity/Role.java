package com.production.monitoring.model.entity;

import com.production.monitoring.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Catálogo persistente de roles. Complementa el enum para mostrar roles desde base de datos.
 */
@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50, columnDefinition = "varchar(50)")
    private UserRole name;

    private String description;
}
