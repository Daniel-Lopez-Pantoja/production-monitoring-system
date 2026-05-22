package com.production.monitoring.service;

import com.production.monitoring.dto.AuthDtos.AuthResponse;
import com.production.monitoring.dto.AuthDtos.LoginRequest;
import com.production.monitoring.dto.AuthDtos.RegisterRequest;
import com.production.monitoring.exception.BusinessRuleException;
import com.production.monitoring.model.entity.User;
import com.production.monitoring.repository.UserRepository;
import com.production.monitoring.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Maneja registro, login y generación de tokens.
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Registra un usuario nuevo validando que el correo no exista.
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("The email address is already registered.");
        }
        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        userRepository.save(user);
        var details = org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword()).roles(user.getRole().name()).build();
        return new AuthResponse(jwtService.generateToken(details), user.getEmail(), user.getFullName(), user.getRole());
    }

    /**
     * Autentica credenciales y devuelve un JWT para consumir endpoints protegidos.
     */
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = userRepository.findByEmail(request.email()).orElseThrow();
        var details = org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword()).roles(user.getRole().name()).build();
        return new AuthResponse(jwtService.generateToken(details), user.getEmail(), user.getFullName(), user.getRole());
    }
}
