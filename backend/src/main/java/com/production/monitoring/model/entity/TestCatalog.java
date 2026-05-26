package com.production.monitoring.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

/**
 * Master catalog of validation tests available for manufacturing workflows.
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

    @Min(1)
    @Column(nullable = false, columnDefinition = "int default 5")
    private Integer estimatedMinMinutes = 5;

    @Min(1)
    @Column(nullable = false, columnDefinition = "int default 15")
    private Integer estimatedMaxMinutes = 15;

    private boolean critical;
}
