package com.optiflow.platform.inventory.interfaces.rest;

import static org.hamcrest.Matchers.endsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optiflow.platform.inventory.domain.model.events.InventoryWasUpdated;
import com.optiflow.platform.inventory.domain.model.events.LowStockAlertGenerated;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * Runs the Store Management & Inventory flow on the Flyway schema, so Hibernate also validates
 * that the V6 migration matches the JPA entities.
 */
@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:inventory_flyway;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;"
        + "DATABASE_TO_LOWER=TRUE",
    "spring.flyway.enabled=true",
    "spring.jpa.hibernate.ddl-auto=validate"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RecordApplicationEvents
class StoreInventoryApiTest {

  private static final String MIRAFLORES_ID = "11111111-1111-1111-1111-111111111111";
  private static final String UNKNOWN_ID = "00000000-0000-0000-0000-000000000000";
  private static final String SKU = "RB-2140-BLK";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ApplicationEvents applicationEvents;

  @Test
  void managesTheCatalogStockAndTheExitOfASoldFrame() throws Exception {
    String supplierId = read(perform(post(ApiV1.path("/suppliers")), """
        {"name":"Distribuidora Andina","contact":"ventas@andina.pe"}
        """).andExpect(status().isCreated())).get("id").asText();
    mockMvc.perform(get(ApiV1.path("/suppliers")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Distribuidora Andina"));

    String frame = """
        {"sku":"%s","name":"Ray-Ban Wayfarer","material":"Acetato","shape":"Cuadrada",
         "color":"Negro","price":320.00}
        """.formatted(SKU);
    perform(post(ApiV1.path("/inventory/frames")), frame)
        .andExpect(status().isCreated())
        .andExpect(header().string(HttpHeaders.LOCATION, endsWith(ApiV1.path("/inventory/frames/" + SKU))))
        .andExpect(jsonPath("$.currency").value("PEN"));
    perform(post(ApiV1.path("/inventory/frames")), frame).andExpect(status().isConflict());
    mockMvc.perform(get(ApiV1.path("/inventory/frames")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].sku").value(SKU));
    perform(patch(ApiV1.path("/inventory/frames/" + SKU + "/price")), "{\"price\":299.90}")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.price").value(299.90));

    perform(post(ApiV1.path("/inventory/replenishments")), """
        {"opticalStoreId":"%s","sku":"%s","supplierId":"%s","quantity":3,"minimumStock":2}
        """.formatted(MIRAFLORES_ID, SKU, supplierId))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.type").value("IN"))
        .andExpect(jsonPath("$.reason").value("REPLENISHMENT"))
        .andExpect(jsonPath("$.quantity").value(3));
    mockMvc.perform(get(ApiV1.path("/inventory/stock")).param("opticalStoreId", MIRAFLORES_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.stocks[0].quantity").value(3))
        .andExpect(jsonPath("$.stocks[0].low").value(false));
    mockMvc.perform(get(ApiV1.path("/inventory/frames/" + SKU)).param("opticalStoreId", MIRAFLORES_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.quantity").value(3))
        .andExpect(jsonPath("$.minimumStock").value(2));

    String saleId = closedSaleWithFrame(MIRAFLORES_ID);

    // Closing the sale took the frame out of the stock (SaleWasClosed -> UpdateInventory).
    mockMvc.perform(get(ApiV1.path("/inventory/stock")).param("opticalStoreId", MIRAFLORES_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.stocks[0].quantity").value(2))
        .andExpect(jsonPath("$.stocks[0].low").value(true));
    mockMvc.perform(get(ApiV1.path("/inventory/alerts")).param("opticalStoreId", MIRAFLORES_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].sku").value(SKU))
        .andExpect(jsonPath("$[0].quantity").value(2));
    String today = LocalDate.now(ZoneId.of("America/Lima")).toString();
    JsonNode movements = read(mockMvc.perform(get(ApiV1.path("/inventory/movements"))
            .param("opticalStoreId", MIRAFLORES_ID)
            .param("from", today)
            .param("to", today)
            .param("sku", SKU))
        .andExpect(status().isOk()));
    assertEquals(2, movements.size());
    JsonNode exit = movements.get(0);
    assertEquals("OUT", exit.get("type").asText());
    assertEquals("SALE", exit.get("reason").asText());
    assertEquals(saleId, exit.get("saleId").asText());
    assertTrue(exit.get("performedBy").isNull());
    assertEquals(1, applicationEvents.stream(InventoryWasUpdated.class).count());
    assertEquals(1, applicationEvents.stream(LowStockAlertGenerated.class).count());
  }

  @Test
  void explainsInvalidInventoryRequests() throws Exception {
    mockMvc.perform(get(ApiV1.path("/inventory/stock")).param("opticalStoreId", UNKNOWN_ID))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("This optical store has no inventory."));
    mockMvc.perform(get(ApiV1.path("/inventory/stock"))).andExpect(status().isBadRequest());
    mockMvc.perform(get(ApiV1.path("/inventory/frames/UNKNOWN-SKU")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Frame model was not found."));
    mockMvc.perform(get(ApiV1.path("/inventory/movements"))
            .param("opticalStoreId", MIRAFLORES_ID)
            .param("from", "2030-02-01")
            .param("to", "2030-01-01"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("The start date must not be after the end date."));
    perform(post(ApiV1.path("/inventory/frames")), """
        {"sku":"NO-PRICE","name":"Montura sin precio","price":0}
        """).andExpect(status().isBadRequest());
  }

  private String closedSaleWithFrame(String storeId) throws Exception {
    String patientId = read(perform(post(ApiV1.path("/patients")), """
        {"name":"Paciente Inventario","email":"inventario@optiflow.test","phone":"987654322",
         "password":"secret12"}
        """).andExpect(status().isCreated())).get("id").asText();
    String timeSlotId = read(mockMvc.perform(get(ApiV1.path("/optical-stores/" + storeId + "/time-slots")))
        .andExpect(status().isOk())).get(0).get("id").asText();
    String appointmentId = read(perform(post(ApiV1.path("/appointments")), """
        {"patientId":"%s","opticalStoreId":"%s","timeSlotId":"%s"}
        """.formatted(patientId, storeId, timeSlotId))
        .andExpect(status().isCreated())).get("id").asText();
    String clinicalRecordId = read(perform(post(ApiV1.path("/clinical-records")), """
        {"patientId":"%s","appointmentId":"%s"}
        """.formatted(patientId, appointmentId))
        .andExpect(status().isCreated())).get("id").asText();
    perform(post(ApiV1.path("/clinical-records/" + clinicalRecordId + "/prescription")), """
        {"sphereOD":-1.25,"cylinderOD":-0.50,"axisOD":90,"sphereOS":-1.00,"cylinderOS":-0.75,
         "axisOS":85,"treatment":"Antirreflejo"}
        """).andExpect(status().isCreated());
    String quotationId = read(perform(post(ApiV1.path("/quotations")), """
        {"clinicalRecordId":"%s","items":[
          {"itemType":"FRAME","productSku":"%s","description":"Montura Ray-Ban",
           "unitPrice":299.90,"quantity":1},
          {"itemType":"LENS","description":"Lunas antirreflejo","unitPrice":169.00,"quantity":1}]}
        """.formatted(clinicalRecordId, SKU)).andExpect(status().isCreated())).get("id").asText();
    perform(patch(ApiV1.path("/quotations/" + quotationId + "/status")), "{\"status\":\"APPROVED\"}")
        .andExpect(status().isOk());
    String saleId = read(perform(post(ApiV1.path("/sales")), "{\"quotationId\":\"%s\"}".formatted(quotationId))
        .andExpect(status().isCreated())).get("id").asText();
    perform(post(ApiV1.path("/sales/" + saleId + "/payments")), "{\"method\":\"CASH\",\"amount\":468.90}")
        .andExpect(status().isCreated());
    perform(patch(ApiV1.path("/sales/" + saleId + "/status")), "{\"status\":\"CLOSED\"}")
        .andExpect(status().isOk());
    return saleId;
  }

  private ResultActions perform(MockHttpServletRequestBuilder request, String body)
      throws Exception {
    return mockMvc.perform(request.contentType(MediaType.APPLICATION_JSON).content(body));
  }

  private JsonNode read(ResultActions result) throws Exception {
    return objectMapper.readTree(result.andReturn().getResponse().getContentAsString());
  }
}
