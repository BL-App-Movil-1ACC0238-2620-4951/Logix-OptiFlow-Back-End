package com.optiflow.platform.production.interfaces.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optiflow.platform.production.domain.events.OrderWasMarkedAsDelivered;
import com.optiflow.platform.production.domain.events.WorkOrderGenerated;
import com.optiflow.platform.production.domain.events.WorkOrderStatusUpdated;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * Runs the Production & Tracking flow, from the booked appointment to the delivered order, on
 * the Flyway schema so Hibernate also validates that the SQL migrations match the entities.
 */
@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:production_flyway;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;"
        + "DATABASE_TO_LOWER=TRUE",
    "spring.flyway.enabled=true",
    "spring.jpa.hibernate.ddl-auto=validate"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RecordApplicationEvents
class ProductionTrackingApiTest {

  private static final String TECHNICIAN_ID = "33333333-3333-3333-3333-333333333333";
  private static final String LABORATORY_ID = "55555555-5555-5555-5555-555555555555";
  private static final String UNKNOWN_ID = "00000000-0000-0000-0000-000000000000";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ApplicationEvents applicationEvents;

  @Test
  void tracksTheOrderFromTheClosedSaleToItsDelivery() throws Exception {
    String patientId = createPatient("valeria.morales@optiflow.test");
    String storeId = firstStoreId();
    String clinicalRecordId = examinedPatientWithPrescription(patientId, storeId, 0);
    String saleId = closedSale(clinicalRecordId);

    // Closing the sale generated the work order (SaleWasClosed -> GenerateWorkOrder).
    JsonNode orders = read(mockMvc.perform(get("/patients/" + patientId + "/work-orders"))
        .andExpect(status().isOk()));
    assertEquals(1, orders.size());
    JsonNode order = orders.get(0);
    String workOrderId = order.get("id").asText();
    assertEquals("PENDING", order.get("status").asText());
    assertEquals(saleId, order.get("saleId").asText());
    assertEquals(storeId, order.get("opticalStoreId").asText());
    assertEquals(2, order.get("lenses").size());
    assertEquals("OD: ESF -1.25 CIL -0.50 EJE 90° | Antirreflejo",
        order.get("lenses").get(0).get("specifications").asText());
    assertEquals(LocalDate.now(ZoneId.of("America/Lima")).plusDays(7).toString(),
        order.get("estimatedDeliveryDate").asText());
    assertEquals(1, applicationEvents.stream(WorkOrderGenerated.class).count());

    perform(post("/work-orders"), "{\"saleId\":\"%s\"}".formatted(saleId))
        .andExpect(status().isConflict());

    mockMvc.perform(get("/technicians"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
    mockMvc.perform(get("/laboratories"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));

    String base = "/work-orders/" + workOrderId;
    perform(patch(base + "/status"), "{\"status\":\"QUALITY_CONTROL\"}")
        .andExpect(status().isConflict());
    perform(patch(base + "/technician"), "{\"technicianId\":\"%s\"}".formatted(UNKNOWN_ID))
        .andExpect(status().isNotFound());
    perform(patch(base + "/technician"), "{\"technicianId\":\"%s\"}".formatted(TECHNICIAN_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.technician.name").value("Jorge Salas"));
    perform(patch(base + "/laboratory"), "{\"laboratoryId\":\"%s\"}".formatted(LABORATORY_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("IN_WORKSHOP"))
        .andExpect(jsonPath("$.laboratory.name").value("Laboratorio Central OptiFlow"));

    perform(patch(base + "/status"), "{\"status\":\"QUALITY_CONTROL\"}")
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message")
            .value("All lenses must be completed before quality control."));
    mockMvc.perform(patch(base + "/lenses/complete"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.lenses[0].completed").value(true))
        .andExpect(jsonPath("$.lenses[1].completed").value(true));
    perform(patch(base + "/status"), "{\"status\":\"QUALITY_CONTROL\"}")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("QUALITY_CONTROL"));

    LocalDate today = LocalDate.now(ZoneId.of("America/Lima"));
    perform(post(base + "/delays"), """
        {"reason":"Falta de stock de lunas","newEstimatedDeliveryDate":"%s"}
        """.formatted(today.minusDays(1))).andExpect(status().isBadRequest());
    perform(post(base + "/delays"), """
        {"reason":"Falta de stock de lunas","newEstimatedDeliveryDate":"%s"}
        """.formatted(today.plusDays(10)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.delayed").value(true))
        .andExpect(jsonPath("$.deliveryDelay.reason").value("Falta de stock de lunas"))
        .andExpect(jsonPath("$.estimatedDeliveryDate").value(today.plusDays(10).toString()));

    mockMvc.perform(patch(base + "/deliver")).andExpect(status().isConflict());
    perform(patch(base + "/status"), "{\"status\":\"READY_FOR_DELIVERY\"}")
        .andExpect(status().isOk());
    perform(patch(base + "/status"), "{\"status\":\"DELIVERED\"}")
        .andExpect(status().isBadRequest());
    mockMvc.perform(patch(base + "/deliver"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("DELIVERED"));
    mockMvc.perform(patch(base + "/deliver")).andExpect(status().isConflict());

    mockMvc.perform(get(base))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.statusHistory.length()").value(5))
        .andExpect(jsonPath("$.statusHistory[4].status").value("DELIVERED"))
        .andExpect(jsonPath("$.deliveredAt").isNotEmpty());
    mockMvc.perform(get("/work-orders").param("status", "DELIVERED"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(workOrderId));
    mockMvc.perform(get("/work-orders").param("technicianId", TECHNICIAN_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
    mockMvc.perform(get("/work-orders").param("status", "PENDING"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
    mockMvc.perform(get("/work-orders").param("status", "BROKEN"))
        .andExpect(status().isBadRequest());

    assertEquals(4, applicationEvents.stream(WorkOrderStatusUpdated.class).count());
    assertEquals(1, applicationEvents.stream(OrderWasMarkedAsDelivered.class).count());
  }

  @Test
  void onlyAClosedSaleCanGenerateAWorkOrder() throws Exception {
    String patientId = createPatient("luis.ramos@optiflow.test");
    String clinicalRecordId = examinedPatientWithPrescription(patientId, firstStoreId(), 1);
    String quotationId = approvedQuotation(clinicalRecordId);
    String saleId = read(perform(post("/sales"), "{\"quotationId\":\"%s\"}".formatted(quotationId))
        .andExpect(status().isCreated())).get("id").asText();

    perform(post("/work-orders"), "{\"saleId\":\"%s\"}".formatted(saleId))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Only a closed sale can generate a work order."));
    perform(post("/work-orders"), "{\"saleId\":\"%s\"}".formatted(UNKNOWN_ID))
        .andExpect(status().isNotFound());
    mockMvc.perform(get("/work-orders/" + UNKNOWN_ID))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Work order was not found."));
    mockMvc.perform(get("/patients/" + patientId + "/work-orders"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  private String createPatient(String email) throws Exception {
    return read(perform(post("/patients"), """
        {"name":"Paciente Producción","email":"%s","phone":"987654321","password":"secret12"}
        """.formatted(email)).andExpect(status().isCreated())).get("id").asText();
  }

  private String firstStoreId() throws Exception {
    return read(mockMvc.perform(get("/optical-stores")).andExpect(status().isOk()))
        .get("opticalStores").get(0).get("id").asText();
  }

  private String examinedPatientWithPrescription(String patientId, String storeId, int slot)
      throws Exception {
    String timeSlotId = read(mockMvc.perform(get("/optical-stores/" + storeId + "/availability"))
        .andExpect(status().isOk())).get("timeSlots").get(slot).get("id").asText();
    String appointmentId = read(perform(post("/appointments"), """
        {"patientId":"%s","opticalStoreId":"%s","timeSlotId":"%s"}
        """.formatted(patientId, storeId, timeSlotId))
        .andExpect(status().isCreated())).get("id").asText();
    String clinicalRecordId = read(perform(post("/clinical-records"), """
        {"patientId":"%s","appointmentId":"%s"}
        """.formatted(patientId, appointmentId))
        .andExpect(status().isCreated())).get("id").asText();
    perform(post("/clinical-records/" + clinicalRecordId + "/prescription"), """
        {"sphereOD":-1.25,"cylinderOD":-0.50,"axisOD":90,"sphereOS":-1.00,"cylinderOS":-0.75,
         "axisOS":85,"treatment":"Antirreflejo"}
        """).andExpect(status().isCreated());
    return clinicalRecordId;
  }

  private String approvedQuotation(String clinicalRecordId) throws Exception {
    String quotationId = read(perform(post("/quotations"), """
        {"clinicalRecordId":"%s","items":[
          {"itemType":"FRAME","description":"Montura Ray-Ban","unitPrice":320.00,"quantity":1},
          {"itemType":"LENS","description":"Lunas antirreflejo","unitPrice":169.00,"quantity":1}]}
        """.formatted(clinicalRecordId)).andExpect(status().isCreated())).get("id").asText();
    mockMvc.perform(patch("/quotations/" + quotationId + "/approve"))
        .andExpect(status().isOk());
    return quotationId;
  }

  private String closedSale(String clinicalRecordId) throws Exception {
    String quotationId = approvedQuotation(clinicalRecordId);
    String saleId = read(perform(post("/sales"), "{\"quotationId\":\"%s\"}".formatted(quotationId))
        .andExpect(status().isCreated())).get("id").asText();
    perform(post("/sales/" + saleId + "/payments"), "{\"method\":\"CASH\",\"amount\":489.00}")
        .andExpect(status().isCreated());
    mockMvc.perform(patch("/sales/" + saleId + "/close")).andExpect(status().isOk());
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
