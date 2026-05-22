package com.production.monitoring.controller;

import com.production.monitoring.dto.AuthDtos.AuthResponse;
import com.production.monitoring.dto.AuthDtos.LoginRequest;
import com.production.monitoring.dto.AuthDtos.RegisterRequest;
import com.production.monitoring.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints públicos de autenticación y registro.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * Inicia sesión y regresa un token JWT.
     */
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /**
     * Registra un usuario nuevo con un rol específico.
     */
    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }
}
