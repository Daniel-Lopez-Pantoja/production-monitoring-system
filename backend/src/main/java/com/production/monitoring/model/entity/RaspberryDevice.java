package com.production.monitoring.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Raspberry asignada para automatización, logging o control de pruebas.
 */
@Getter
@Setter
@Entity
@Table(name = "raspberry_devices")
public class RaspberryDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String ipAddress;
    private String rack;
    private boolean available = true;
}
