package com.optiflow.platform.shared.infrastructure.security;

import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("!test")
public class SecurityConfiguration {

  private static final String PATIENT = "PATIENT";
  private static final String OPTOMETRIST = "OPTOMETRIST";
  private static final String SALES_ADVISOR = "SALES_ADVISOR";
  private static final String TECHNICIAN = "TECHNICIAN";
  private static final String ADMIN = "ADMIN";

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final RestAuthenticationEntryPoint authenticationEntryPoint;
  private final RestAccessDeniedHandler accessDeniedHandler;

  public SecurityConfiguration(
      JwtAuthenticationFilter jwtAuthenticationFilter,
      RestAuthenticationEntryPoint authenticationEntryPoint,
      RestAccessDeniedHandler accessDeniedHandler) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.authenticationEntryPoint = authenticationEntryPoint;
    this.accessDeniedHandler = accessDeniedHandler;
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(HttpMethod.POST, ApiV1.path("/patients")).permitAll()
            .requestMatchers(HttpMethod.POST, ApiV1.path("/auth/tokens")).permitAll()
            .requestMatchers(HttpMethod.POST, ApiV1.path("/payment-notifications")).permitAll()
            .requestMatchers(
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs",
                "/v3/api-docs/**")
            .permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/optical-stores/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/staff-members/*")
            .hasAnyRole(OPTOMETRIST, SALES_ADVISOR, TECHNICIAN, ADMIN)
            .requestMatchers("/api/v1/staff-members/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/v1/optical-stores/*/time-slots")
            .hasRole("ADMIN")
            // Search & Booking
            .requestMatchers(HttpMethod.POST, "/api/v1/appointments")
            .hasAnyRole(PATIENT, OPTOMETRIST, SALES_ADVISOR, ADMIN)
            .requestMatchers(HttpMethod.GET, "/api/v1/appointments").hasAnyRole(OPTOMETRIST, ADMIN)
            .requestMatchers("/api/v1/appointments/**").hasAnyRole(PATIENT, OPTOMETRIST, ADMIN)
            .requestMatchers(HttpMethod.GET, "/api/v1/patients")
            .hasAnyRole(OPTOMETRIST, SALES_ADVISOR, ADMIN)
            .requestMatchers(HttpMethod.GET, "/api/v1/patients/*")
            .hasAnyRole(PATIENT, OPTOMETRIST, SALES_ADVISOR, ADMIN)
            .requestMatchers("/api/v1/patients/*/appointments", "/api/v1/patients/*/favorites")
            .hasAnyRole(PATIENT, ADMIN)
            .requestMatchers(HttpMethod.POST, "/api/v1/optical-stores/*/ratings")
            .hasAnyRole(PATIENT, ADMIN)
            // Clinical & Commercial
            .requestMatchers(HttpMethod.GET, "/api/v1/clinical-records/summary")
            .hasAnyRole(OPTOMETRIST, SALES_ADVISOR, ADMIN)
            .requestMatchers(HttpMethod.GET, "/api/v1/clinical-records/**",
                "/api/v1/patients/*/clinical-records")
            .hasAnyRole(PATIENT, OPTOMETRIST, SALES_ADVISOR, ADMIN)
            .requestMatchers("/api/v1/clinical-records/**").hasAnyRole(OPTOMETRIST, ADMIN)
            .requestMatchers(HttpMethod.GET, "/api/v1/quotations/*", "/api/v1/sales/*")
            .hasAnyRole(PATIENT, SALES_ADVISOR, ADMIN)
            .requestMatchers("/api/v1/quotations/**", "/api/v1/sales/**")
            .hasAnyRole(SALES_ADVISOR, ADMIN)
            // Production & Tracking
            .requestMatchers(HttpMethod.GET, "/api/v1/patients/*/work-orders")
            .hasAnyRole(PATIENT, TECHNICIAN, ADMIN)
            .requestMatchers(HttpMethod.POST, "/api/v1/work-orders")
            .hasAnyRole(TECHNICIAN, SALES_ADVISOR, ADMIN)
            .requestMatchers(HttpMethod.GET, "/api/v1/work-orders", "/api/v1/work-orders/*")
            .hasAnyRole(TECHNICIAN, OPTOMETRIST, SALES_ADVISOR, ADMIN)
            .requestMatchers("/api/v1/work-orders/**", "/api/v1/technicians", "/api/v1/laboratories")
            .hasAnyRole(TECHNICIAN, ADMIN)
            .anyRequest()
            .authenticated())
        .exceptionHandling(handler -> handler
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}
