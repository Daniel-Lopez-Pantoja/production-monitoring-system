package com.production.monitoring.repository;

import com.production.monitoring.model.entity.RaspberryDevice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RaspberryDeviceRepository extends JpaRepository<RaspberryDevice, Long> {
    boolean existsByName(String name);
}
