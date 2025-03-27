package com.rustambek.payment.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@io.swagger.v3.oas.annotations.security.SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(servers());
    }
    private List<Server> servers() {
        return List.of(
                new Server()
                        .url("http://127.0.0.1:8080")
                        .description("Local Server"));

    }

    private Info apiInfo() {
        return new Info()
                .title("Payment API")
                .description("API for Payment")
                .version("1.0")
                .contact(apiContact());
    }

    private Contact apiContact() {
        return new Contact()
                .name("Oripov Rustambek")
                .email("herofirst4464@gmail.com")
                .url("https://t.me/rustam4464");
    }

}
