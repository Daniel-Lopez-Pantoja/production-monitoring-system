package com.production.monitoring.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.production.monitoring.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Usuario del sistema. Se usa para autenticar, autorizar y asignar responsables.
 */
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50, columnDefinition = "varchar(50)")
    private UserRole role;

    @Column(nullable = false)
    private boolean active = true;
}
