package com.production.monitoring.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Catálogo maestro de pruebas que pueden ejecutarse sobre servidores.
 */
@Getter
@Setter
@Entity
@Table(name = "test_catalog")
public class TestCatalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1600)
    private String validates;

    @Column(length = 1600)
    private String possibleFailures;

    private boolean critical;
}
