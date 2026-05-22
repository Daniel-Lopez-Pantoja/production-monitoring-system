package com.production.monitoring.repository;

import com.production.monitoring.model.entity.TestCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestCatalogRepository extends JpaRepository<TestCatalog, Long> {
    boolean existsByName(String name);
}
