package com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration;

import com.optiflow.platform.shared.interfaces.rest.ApiError;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.List;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

  public static final String BEARER_SCHEME = "bearerAuth";
  private static final String API_ERROR = "ApiError";

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
            tag(OpenApiTags.SUPPLIERS, "Suppliers that deliver the replenishments."),
            tag(OpenApiTags.INVENTORY_REPORTS,
                "Inventory metrics of a store for the admin, in JSON or CSV."),
            tag(OpenApiTags.NOTIFICATIONS,
                "Notifications of a patient: reminders, order updates and surveys."),
            tag(OpenApiTags.LOYALTY_ACCOUNTS, "Loyalty points of a patient.")));
  }

  /**
   * Documents the error responses shared by every route once: 400 on routes with input, 401 and
   * 403 on secured routes, and the {@link ApiError} body of every 4xx and 5xx response, which springdoc
   * would otherwise fill with the return type of the method.
   */
  @Bean
  OpenApiCustomizer apiErrorResponses() {
    return openApi -> {
      openApi.getComponents().addSchemas(API_ERROR,
          ModelConverters.getInstance().readAllAsResolvedSchema(ApiError.class).schema);
      openApi.getPaths().values().forEach(path -> path.readOperations().forEach(operation -> {
        ApiResponses responses = operation.getResponses();
        boolean hasInput = operation.getRequestBody() != null
            || (operation.getParameters() != null && !operation.getParameters().isEmpty());
        if (hasInput) {
          responses.putIfAbsent("400",
              new ApiResponse().description("The request data is invalid."));
        }
        if (operation.getSecurity() == null) {
          responses.putIfAbsent("401", new ApiResponse()
              .description("The access token is missing, invalid or expired."));
          responses.putIfAbsent("403", new ApiResponse()
              .description("The role of the user cannot use this route."));
        }
        responses.forEach((code, response) -> {
          if (code.startsWith("4") || code.startsWith("5")) {
            response.setContent(new Content().addMediaType(
                org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                new MediaType().schema(new Schema<>().$ref("#/components/schemas/" + API_ERROR))));
          }
        });
      }));
    };
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
