package com.optiflow.platform.shared.documentation.openapi.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

  @Bean
  OpenAPI optiFlowOpenApi() {
    return new OpenAPI()
        .info(new Info()
            .title("OptiFlow Platform API")
            .version("0.1.0")
            .description("RESTful services for the OptiFlow bounded contexts."))
        .tags(List.of(
            tag(OpenApiTags.PATIENTS, "Patient registration, login and appointment history."),
            tag(OpenApiTags.OPTICAL_STORES, "Search and filter optical stores."),
            tag(OpenApiTags.AVAILABILITY, "Available time slots of an optical store."),
            tag(OpenApiTags.APPOINTMENTS, "Book and consult appointments."),
            tag(OpenApiTags.FAVORITES, "Optical stores saved as favorites by a patient."),
            tag(OpenApiTags.RATINGS, "Patient ratings of optical stores."),
            tag(OpenApiTags.CLINICAL_RECORDS,
                "Patient examination, medical history and optical prescription."),
            tag(OpenApiTags.QUOTATIONS, "Quotations derived from the prescription."),
            tag(OpenApiTags.SALES, "Sales, payments and electronic receipts."),
            tag(OpenApiTags.WORK_ORDERS,
                "Work orders generated from closed sales: Kanban board, lenses and delivery."),
            tag(OpenApiTags.TECHNICIANS_AND_LABORATORIES,
                "Technicians and laboratories that can work on an order.")));
  }

  private static Tag tag(String name, String description) {
    return new Tag().name(name).description(description);
  }
}
