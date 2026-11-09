package com.optiflow.platform.shared.infrastructure.documentation.openapi;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiDocumentationTest {

  private static final String REPLENISH = "$.paths['/api/v1/inventory/replenishments'].post";
  private static final String REPORT = "$.paths['/api/v1/reports/inventory'].get.responses['200']";
  private static final String WEBHOOK = "$.paths['/api/v1/payment-notifications'].post";
  private static final String ERROR_REF = "#/components/schemas/ApiError";

  @Autowired
  private MockMvc mockMvc;

  @Test
  void documentsTheOperationAndItsErrorResponses() throws Exception {
    mockMvc.perform(get("/v3/api-docs"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.components.schemas.ApiError.properties", hasKey("message")))
        .andExpect(jsonPath(REPLENISH + ".summary").value("Replenish stock"))
        .andExpect(jsonPath(REPLENISH + ".responses", hasKey("201")))
        .andExpect(jsonPath(REPLENISH + ".responses['201'].content").exists())
        .andExpect(jsonPath(REPLENISH + ".responses['404'].content['application/json'].schema['$ref']")
            .value(ERROR_REF))
        .andExpect(jsonPath(REPLENISH + ".responses['400'].content['application/json'].schema['$ref']")
            .value(ERROR_REF))
        .andExpect(jsonPath(REPLENISH + ".responses", hasKey("401")))
        .andExpect(jsonPath(REPLENISH + ".responses", hasKey("403")))
        .andExpect(jsonPath("$.paths['/api/v1/suppliers'].get.responses", not(hasKey("400"))))
        .andExpect(jsonPath(REPORT + ".content", hasKey("application/json")))
        .andExpect(jsonPath(REPORT + ".content", hasKey("text/csv")))
        .andExpect(jsonPath(WEBHOOK + ".responses", hasKey("401")))
        .andExpect(jsonPath(WEBHOOK + ".responses", not(hasKey("403"))));
  }
}
