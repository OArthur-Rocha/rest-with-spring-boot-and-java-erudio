package com.example.restwithspringbootandjavaerudio.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("RESTful API with Java 17 and Spring 3")
                        .version("v1")
                        .description("Some description about this API")
                        .termsOfService("http://erudio.api/license")
                        .license(new License().name("Apache 2.0")
                                .url("http://erudio.api/license")));
    }

}
