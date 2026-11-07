package com.optiflow.platform.searchbooking.interfaces.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SearchAndBookingApiTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void searchesStoresAndRejectsASecondBookingForTheSameSlot() throws Exception {
    MvcResult stores = mockMvc.perform(get(ApiV1.path("/optical-stores")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andReturn();
    String storeId = objectMapper.readTree(stores.getResponse().getContentAsString())
        .get(0).get("id").asText();

    MvcResult availability = mockMvc.perform(get(ApiV1.path("/optical-stores/" + storeId + "/availability")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(3))
        .andReturn();
    String timeSlotId = objectMapper.readTree(availability.getResponse().getContentAsString())
        .get(0).get("id").asText();

    MvcResult patient = mockMvc.perform(post(ApiV1.path("/patients"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"name":"Ana Perez","email":"ana.perez@optiflow.test","phone":"999888777","password":"secret12"}
                """))
        .andExpect(status().isCreated())
        .andReturn();
    String patientId = objectMapper.readTree(patient.getResponse().getContentAsString())
        .get("id").asText();

    String booking = """
        {"patientId":"%s","opticalStoreId":"%s","timeSlotId":"%s"}
        """.formatted(patientId, storeId, timeSlotId);

    mockMvc.perform(post(ApiV1.path("/appointments")).contentType(MediaType.APPLICATION_JSON).content(booking))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("CONFIRMED"));

    mockMvc.perform(post(ApiV1.path("/appointments")).contentType(MediaType.APPLICATION_JSON).content(booking))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("The selected time slot is no longer available."));

    mockMvc.perform(get(ApiV1.path("/optical-stores")).param("name", "does-not-exist"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void rejectsInvalidLoginCredentials() throws Exception {
    mockMvc.perform(post(ApiV1.path("/patients"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"name":"Luis Ramos","email":"luis.ramos@optiflow.test","phone":"999111222","password":"secret12"}
                """))
        .andExpect(status().isCreated());

    mockMvc.perform(post(ApiV1.path("/login"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"email":"luis.ramos@optiflow.test","password":"wrong-password"}
                """))
        .andExpect(status().isUnauthorized());
  }
  @Test
  void rejectsPhoneLettersWithoutCreatingThePatient() throws Exception {
    mockMvc.perform(post(ApiV1.path("/patients"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"name":"Phone Test","email":"phone.invalid@optiflow.test","phone":"abc999888777","password":"secret12"}
                """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value(
            "Phone number must contain 7 to 15 digits, with an optional leading +, spaces or hyphens."));

    // The rejected request must not consume the email or persist a patient.
    mockMvc.perform(post(ApiV1.path("/patients"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"name":"Phone Test","email":"phone.invalid@optiflow.test","phone":"999888777","password":"secret12"}
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.phone").value("999888777"));
  }

  @Test
  void acceptsAndNormalizesInternationalPhoneFormatting() throws Exception {
    mockMvc.perform(post(ApiV1.path("/patients"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"name":"Formatted Phone","email":"phone.formatted@optiflow.test","phone":"+51 999-888-777","password":"secret12"}
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.phone").value("51999888777"));
  }
}
