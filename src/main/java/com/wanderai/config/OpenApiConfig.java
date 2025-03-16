package com.wanderai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI wanderAiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WanderAI API")
                        .description("WanderAI Travel Planning Platform API Documentation")
                        .version("v0.1.0")
                        .contact(new Contact()
                                .name("WanderAI Team")
                                .email("support@wanderai.com")));
    }
}