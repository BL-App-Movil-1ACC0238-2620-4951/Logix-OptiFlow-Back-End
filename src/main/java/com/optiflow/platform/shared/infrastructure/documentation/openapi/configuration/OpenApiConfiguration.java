package com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

  public static final String BEARER_SCHEME = "bearerAuth";

  @Bean
  OpenAPI optiFlowOpenApi() {
    return new OpenAPI()
        .info(new Info()
            .title("OptiFlow Platform API")
            .version("0.1.0")
            .description("RESTful services for the OptiFlow bounded contexts."))
        .components(new Components().addSecuritySchemes(BEARER_SCHEME, bearerSecurityScheme()))
        .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
        .tags(List.of(
            tag(OpenApiTags.AUTHENTICATION, "Issue JWT access tokens for patients and staff."),
            tag(OpenApiTags.PATIENTS, "Patient registration and appointment history."),
            tag(OpenApiTags.OPTICAL_STORES, "Search and filter optical stores."),
            tag(OpenApiTags.AVAILABILITY, "Available time slots of an optical store."),
            tag(OpenApiTags.APPOINTMENTS, "Book and consult appointments."),
            tag(OpenApiTags.STAFF_MEMBERS, "Staff registration, listing and role assignment."),
            tag(OpenApiTags.FAVORITES, "Optical stores saved as favorites by a patient."),
            tag(OpenApiTags.RATINGS, "Patient ratings of optical stores."),
            tag(OpenApiTags.CLINICAL_RECORDS,
                "Patient examination, medical history and optical prescription."),
            tag(OpenApiTags.QUOTATIONS, "Quotations derived from the prescription."),
            tag(OpenApiTags.SALES, "Sales, payments and electronic receipts."),
            tag(OpenApiTags.WORK_ORDERS,
                "Work orders generated from closed sales: Kanban board, lenses and delivery."),
            tag(OpenApiTags.TECHNICIANS_AND_LABORATORIES,
                "Technicians and laboratories that can work on an order."),
            tag(OpenApiTags.FRAME_MODELS,
                "Frame catalog, scanned frames and prices."),
            tag(OpenApiTags.INVENTORY,
                "Stock of each optical store, replenishments, movements and low stock alerts."),
            tag(OpenApiTags.SUPPLIERS, "Suppliers that deliver the replenishments.")));
  }

  private static Tag tag(String name, String description) {
    return new Tag().name(name).description(description);
  }

  private static SecurityScheme bearerSecurityScheme() {
    return new SecurityScheme()
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT")
        .description(
            "JWT obtained from POST /api/v1/auth/tokens. Send as: Authorization: Bearer <token>");
  }
}
