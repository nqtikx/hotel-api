package com.example.hotelapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hotelApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hotel API")
                        .version("1.0.0")
                        .description("REST API для работы с отелями")
                        .contact(new Contact()
                                .name("Nikitikk")
                                .email("miracleqxz@gmail.com")
                        )
                );
    }
}
