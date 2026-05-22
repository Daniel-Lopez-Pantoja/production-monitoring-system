package com.production.monitoring.repository;

import com.production.monitoring.model.entity.Role;
import com.production.monitoring.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByName(UserRole name);
}
