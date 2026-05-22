package com.production.monitoring.dto;

import com.production.monitoring.model.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTOs usados por login y registro. Mantienen separada la API de las entidades de base de datos.
 */
public class AuthDtos {
    public record LoginRequest(@Email String email, @NotBlank String password) {}
    public record RegisterRequest(@NotBlank String fullName, @Email String email, @NotBlank String password, @NotNull UserRole role) {}
    public record AuthResponse(String token, String email, String fullName, UserRole role) {}
}
