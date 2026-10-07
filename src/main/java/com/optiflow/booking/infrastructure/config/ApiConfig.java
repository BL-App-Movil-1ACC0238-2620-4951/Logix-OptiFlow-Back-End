package com.optiflow.booking.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins("*")
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE");
  }

  @Bean
  OpenAPI searchBookingOpenApi() {
    return new OpenAPI().info(new Info()
        .title("OptiFlow Search & Booking API")
        .version("0.1.0")
        .description("RESTful services for optical store search and appointment booking."));
  }
}
