package br.com.unasp.comandadigital.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI comandaDigitalOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Comanda Digital API")
                        .description("Sistema de Pedidos para Dark Kitchen - UNASP SP")
                        .version("1.0.0")
                        .contact(new Contact().name("UNASP SP - Desenvolvimento Full-Stack")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME))
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME,
                        new SecurityScheme()
                                .name(SECURITY_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Cole o token JWT obtido em POST /api/auth/login")));
    }
}
