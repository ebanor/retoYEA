package com.mikeldi.reto.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingresa el token JWT en el formato: Bearer {token}")
                        )
                )
                .info(new Info()
                        .title("ProyectoRetoYEA - API ERP")
                        .version("1.0.0")
                        .description("API REST para sistema ERP - Gestión empresarial completa\n\n" +
                                "### Autenticación\n" +
                                "Esta API usa JWT (JSON Web Token) para autenticación.\n\n" +
                                "**Pasos para autenticarte:**\n" +
                                "1. Obtén un token usando el endpoint `/api/auth/login`\n" +
                                "2. Haz clic en el botón **Authorize** arriba\n" +
                                "3. Ingresa: `Bearer {tu-token-jwt}`\n" +
                                "4. Ahora puedes usar los endpoints protegidos\n\n" +
                                "### Roles disponibles\n" +
                                "- **ADMIN**: Acceso completo al sistema\n" +
                                "- **COMERCIAL**: Gestión de clientes y pedidos\n" +
                                "- **ALMACEN**: Gestión de productos y stock")
                        .contact(new Contact()
                                .name("Equipo Mikeldi")
                                .email("desarrollo@mikeldi.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
