package com.production.monitoring.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * PDU usada para controlar y monitorear energía de servidores en rack.
 */
@Getter
@Setter
@Entity
@Table(name = "pdus")
public class Pdu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String ipAddress;
    private String rack;
    private String location;
}
