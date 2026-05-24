package com.production.monitoring.config;

import com.production.monitoring.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configura seguridad stateless con JWT, roles y CORS para React.
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        // DEMO_USER es read-only para proteger la demo pública y evitar cambios destructivos.
                        .requestMatchers(HttpMethod.GET, "/api/dashboard").hasAnyRole("ADMIN", "ENGINEER", "TECHNICIAN", "OPERATOR", "DEMO_USER")
                        .requestMatchers(HttpMethod.GET, "/api/servers").hasAnyRole("ADMIN", "ENGINEER", "TECHNICIAN", "OPERATOR", "DEMO_USER")
                        .requestMatchers(HttpMethod.GET, "/api/servers/**").hasAnyRole("ADMIN", "ENGINEER", "TECHNICIAN", "OPERATOR", "DEMO_USER")
                        .requestMatchers(HttpMethod.GET, "/api/tests").hasAnyRole("ADMIN", "ENGINEER", "TECHNICIAN", "OPERATOR", "DEMO_USER")
                        .requestMatchers(HttpMethod.GET, "/api/server-tests").hasAnyRole("ADMIN", "ENGINEER", "TECHNICIAN", "OPERATOR", "DEMO_USER")
                        .requestMatchers(HttpMethod.GET, "/api/traceability").hasAnyRole("ADMIN", "ENGINEER", "TECHNICIAN", "OPERATOR", "DEMO_USER")
                        .requestMatchers(HttpMethod.GET, "/api/failures").hasAnyRole("ADMIN", "ENGINEER", "TECHNICIAN", "OPERATOR", "DEMO_USER")
                        .requestMatchers(HttpMethod.GET, "/api/reports/**").hasAnyRole("ADMIN", "ENGINEER", "TECHNICIAN", "OPERATOR", "DEMO_USER")
                        .requestMatchers(HttpMethod.GET, "/api/pdus").hasAnyRole("ADMIN", "ENGINEER", "TECHNICIAN", "OPERATOR", "DEMO_USER")
                        .requestMatchers(HttpMethod.GET, "/api/raspberries").hasAnyRole("ADMIN", "ENGINEER", "TECHNICIAN", "OPERATOR", "DEMO_USER")
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/auth/register").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
            "http://localhost:5173",
            "http://localhost:5174",
            "http://localhost:5175",
            "https://production-monitoring-system.vercel.app",
            "https://production-monitoring-system-gamma.vercel.app"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
