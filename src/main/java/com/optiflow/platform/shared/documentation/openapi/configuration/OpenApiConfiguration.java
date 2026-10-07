package com.optiflow.platform.shared.documentation.openapi.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

  @Bean
  OpenAPI optiFlowOpenApi() {
    return new OpenAPI().info(new Info()
        .title("OptiFlow Platform API")
        .version("0.1.0")
        .description("RESTful services for the OptiFlow bounded contexts."));
  }
}
