package com.production.monitoring.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configura la documentación Swagger/OpenAPI visible en /swagger-ui.html.
 */
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info()
                .title("Production Monitoring System API")
                .version("1.0.0")
                .description("API para trazabilidad, pruebas y fallas de servidores en manufactura."));
    }
}
