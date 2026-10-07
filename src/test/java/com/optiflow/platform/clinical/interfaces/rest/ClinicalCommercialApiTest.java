package com.optiflow.platform.clinical.interfaces.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optiflow.platform.clinical.domain.events.SaleWasClosed;
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
 * Runs the whole Clinical & Commercial flow on the Flyway schema, so Hibernate also validates
 * that the SQL migrations match the JPA entities.
 */
@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:clinical_flyway;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;"
        + "DATABASE_TO_LOWER=TRUE",
    "spring.flyway.enabled=true",
    "spring.jpa.hibernate.ddl-auto=validate"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RecordApplicationEvents
class ClinicalCommercialApiTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ApplicationEvents applicationEvents;

  @Test
  void attendsAPatientFromTheBookedAppointmentToTheClosedSale() throws Exception {
    String patientId = createPatient();
    String appointmentId = bookAppointment(patientId);

    // Booking the appointment opened a pending clinical record (AppointmentBooked -> ExaminePatient).
    JsonNode records = read(mockMvc.perform(get("/patients/" + patientId + "/clinical-records"))
        .andExpect(status().isOk()));
    assertEquals(1, records.size());
    assertEquals("PENDING", records.get(0).get("status").asText());
    String pendingRecordId = records.get(0).get("id").asText();

    String registration = """
        {"patientId":"%s","appointmentId":"%s","observations":"Visión borrosa de lejos"}
        """.formatted(patientId, appointmentId);
    String clinicalRecordId = read(perform(post("/clinical-records"), registration)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("OPEN")))
        .get("id").asText();
    assertEquals(pendingRecordId, clinicalRecordId);
    perform(post("/clinical-records"), registration).andExpect(status().isConflict());

    perform(put("/clinical-records/" + clinicalRecordId + "/medical-history"), """
        {"allergies":["Penicilina"],"previousConditions":["Miopía"],"familyOcularHistory":"Glaucoma"}
        """)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.medicalHistory.allergies[0]").value("Penicilina"));

    String quotationRequest = """
        {"clinicalRecordId":"%s","items":[
          {"itemType":"FRAME","productSku":"RB-2140-BLK","description":"Montura Ray-Ban Wayfarer",
           "unitPrice":320.00,"quantity":1},
          {"itemType":"LENS","description":"Lunas policarbonato con antirreflejo",
           "unitPrice":169.00,"quantity":1}]}
        """.formatted(clinicalRecordId);
    perform(post("/quotations"), quotationRequest).andExpect(status().isConflict());

    perform(post("/clinical-records/" + clinicalRecordId + "/prescription"), prescription(181))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Axis OD must be between 0 and 180 degrees."));
    perform(post("/clinical-records/" + clinicalRecordId + "/prescription"), prescription(90))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.axisOD").value(90));
    mockMvc.perform(get("/clinical-records/" + clinicalRecordId + "/prescription"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.sphereOD").value(-1.25));

    String quotationId = read(perform(post("/quotations"), quotationRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("DRAFT"))
        .andExpect(jsonPath("$.total").value(489.00))
        .andExpect(jsonPath("$.currency").value("PEN")))
        .get("id").asText();

    perform(patch("/quotations/" + quotationId + "/discount"), """
        {"type":"PERCENTAGE","value":10,"reason":"Campaña escolar"}
        """)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.discount.amount").value(48.90))
        .andExpect(jsonPath("$.total").value(440.10));

    String sale = """
        {"quotationId":"%s"}
        """.formatted(quotationId);
    perform(post("/sales"), sale).andExpect(status().isConflict());
    mockMvc.perform(patch("/quotations/" + quotationId + "/approve"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("APPROVED"));

    String saleId = read(perform(post("/sales"), sale)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"))
        .andExpect(jsonPath("$.patientId").value(patientId)))
        .get("id").asText();
    perform(post("/sales"), sale).andExpect(status().isConflict());

    perform(post("/sales/" + saleId + "/payments"), """
        {"method":"YAPE","amount":400.00}
        """).andExpect(status().isBadRequest());
    perform(post("/sales/" + saleId + "/payments"), """
        {"method":"BITCOIN","amount":440.10}
        """)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Payment method must be one of: CASH, CARD, YAPE, PLIN."));
    perform(post("/sales/" + saleId + "/payments"), """
        {"method":"YAPE","amount":440.10,"transactionReference":"YAPE-778899"}
        """)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("PAID"))
        .andExpect(jsonPath("$.payment.transactionReference").value("YAPE-778899"));

    mockMvc.perform(patch("/sales/" + saleId + "/close"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CLOSED"))
        .andExpect(jsonPath("$.receipt.receiptNumber").value("B001-00000001"))
        .andExpect(jsonPath("$.receipt.taxAmount").value(67.13))
        .andExpect(jsonPath("$.receipt.totalAmount").value(440.10));
    mockMvc.perform(get("/sales/" + saleId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.receipt.receiptNumber").value("B001-00000001"));
    mockMvc.perform(patch("/sales/" + saleId + "/close")).andExpect(status().isConflict());

    assertEquals(1, applicationEvents.stream(SaleWasClosed.class).count());
  }

  @Test
  void returnsNotFoundForUnknownResources() throws Exception {
    String unknownId = "00000000-0000-0000-0000-000000000000";

    mockMvc.perform(get("/clinical-records/" + unknownId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Clinical record was not found."));
    mockMvc.perform(get("/quotations/" + unknownId)).andExpect(status().isNotFound());
    mockMvc.perform(get("/sales/" + unknownId)).andExpect(status().isNotFound());
    perform(post("/clinical-records"), """
        {"patientId":"%s","appointmentId":"%s"}
        """.formatted(unknownId, unknownId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Appointment was not found."));
  }

  private String createPatient() throws Exception {
    return read(perform(post("/patients"), """
        {"name":"Valeria Morales","email":"valeria.morales@optiflow.test","phone":"987654321",
         "password":"secret12"}
        """).andExpect(status().isCreated())).get("id").asText();
  }

  private String bookAppointment(String patientId) throws Exception {
    JsonNode stores = read(mockMvc.perform(get("/optical-stores")).andExpect(status().isOk()));
    String storeId = stores.get("opticalStores").get(0).get("id").asText();
    JsonNode availability = read(mockMvc.perform(
        get("/optical-stores/" + storeId + "/availability")).andExpect(status().isOk()));
    String timeSlotId = availability.get("timeSlots").get(0).get("id").asText();
    return read(perform(post("/appointments"), """
        {"patientId":"%s","opticalStoreId":"%s","timeSlotId":"%s"}
        """.formatted(patientId, storeId, timeSlotId))
        .andExpect(status().isCreated())).get("id").asText();
  }

  private static String prescription(int axisOd) {
    return """
        {"sphereOD":-1.25,"cylinderOD":-0.50,"axisOD":%d,"sphereOS":-1.00,"cylinderOS":-0.75,
         "axisOS":85,"treatment":"Antirreflejo"}
        """.formatted(axisOd);
  }

  private ResultActions perform(MockHttpServletRequestBuilder request, String body)
      throws Exception {
    return mockMvc.perform(request.contentType(MediaType.APPLICATION_JSON).content(body));
  }

  private JsonNode read(ResultActions result) throws Exception {
    return objectMapper.readTree(result.andReturn().getResponse().getContentAsString());
  }
}
