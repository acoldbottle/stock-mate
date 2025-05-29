package com.acoldbottle.stockmate.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("StockMate API Docs")
                        .description("개인 프로젝트 StockMate API 목록입니다.")
                        .version("v1.0"));
    }
}
