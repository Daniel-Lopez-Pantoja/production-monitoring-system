package com.production.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada principal de Spring Boot.
 * Desde aquí se inicia todo el backend del sistema de monitoreo.
 */
@SpringBootApplication
public class ProductionMonitoringApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductionMonitoringApplication.class, args);
    }
}
