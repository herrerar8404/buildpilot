package com.acdiorr.buildpilot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI buildPilotOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BuildPilot API")
                        .description("REST API for construction design and estimation — projects and rooms management.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("BuildPilot Team")
                                .email("contact@buildpilot.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local development server")
                ));
    }
}

