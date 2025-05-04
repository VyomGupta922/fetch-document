package com.docment.fetch.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI documentApi() {
        return new OpenAPI().info(new Info().title("Document Management API")
                .description("APIs for Upload, Search, and Q&A")
                .version("1.0"));
    }
}
