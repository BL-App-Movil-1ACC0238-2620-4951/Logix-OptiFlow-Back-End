package com.optiflow.platform.shared.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("secured-test")
class AuthenticationAuthorizationApiTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void rejectsProtectedRequestsWithoutAToken() throws Exception {
    mockMvc.perform(post(ApiV1.path("/appointments"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"patientId":"00000000-0000-0000-0000-000000000001",
                 "opticalStoreId":"11111111-1111-1111-1111-111111111111",
                 "timeSlotId":"00000000-0000-0000-0000-000000000002"}
                """))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401));
  }

  @Test
  void allowsPatientBookingWithTokenAndBlocksStaffRoutes() throws Exception {
    String patientToken = registerPatientAndIssueToken(
        "Secured Patient", "secured.patient@optiflow.test", "999333444");

    MvcResult stores = mockMvc.perform(get(ApiV1.path("/optical-stores")))
        .andExpect(status().isOk())
        .andReturn();
    String storeId = objectMapper.readTree(stores.getResponse().getContentAsString())
        .get(0).get("id").asText();

    MvcResult slots = mockMvc.perform(get(ApiV1.path("/optical-stores/" + storeId + "/time-slots")))
        .andExpect(status().isOk())
        .andReturn();
    String timeSlotId = objectMapper.readTree(slots.getResponse().getContentAsString())
        .get(0).get("id").asText();

    String patientId = objectMapper.readTree(
            mockMvc.perform(post(ApiV1.path("/patients"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"name":"Other","email":"other.patient@optiflow.test","phone":"999333445","password":"secret12"}
                        """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString())
        .get("id")
        .asText();

    String booking = """
        {"patientId":"%s","opticalStoreId":"%s","timeSlotId":"%s"}
        """.formatted(patientId, storeId, timeSlotId);

    mockMvc.perform(post(ApiV1.path("/appointments"))
            .header(HttpHeaders.AUTHORIZATION, bearer(patientToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content(booking))
        .andExpect(status().isCreated());

    mockMvc.perform(get(ApiV1.path("/staff-members")))
        .header(HttpHeaders.AUTHORIZATION, bearer(patientToken))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status").value(403));
  }

  @Test
  void forbidsPatientFromReadingAnotherPatientsAppointments() throws Exception {
    String ownerToken = registerPatientAndIssueToken(
        "Owner Patient", "owner.patient@optiflow.test", "999222111");
    JsonNode ownerLogin = objectMapper.readTree(
        mockMvc.perform(post(ApiV1.path("/auth/tokens"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"owner.patient@optiflow.test","password":"secret12"}
                    """))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString());
    String ownerId = ownerLogin.get("userId").asText();

    String otherPatientId = objectMapper.readTree(
            mockMvc.perform(post(ApiV1.path("/patients"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"name":"Other Owner","email":"other.owner@optiflow.test","phone":"999222112","password":"secret12"}
                        """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString())
        .get("id")
        .asText();

    mockMvc.perform(get(ApiV1.path("/patients/" + otherPatientId + "/appointments"))
            .header(HttpHeaders.AUTHORIZATION, bearer(ownerToken)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("You cannot access another patient's data."));

    mockMvc.perform(get(ApiV1.path("/patients/" + ownerId + "/appointments"))
            .header(HttpHeaders.AUTHORIZATION, bearer(ownerToken)))
        .andExpect(status().isOk());
  }

  @Test
  void allowsAdminToListStaffMembers() throws Exception {
    String adminToken = issueTokenFor("admin@optiflow.test");

    mockMvc.perform(get(ApiV1.path("/staff-members")))
        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].role").value("ADMIN"));
  }

  private String registerPatientAndIssueToken(String name, String email, String phone)
      throws Exception {
    mockMvc.perform(post(ApiV1.path("/patients"))
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"name":"%s","email":"%s","phone":"%s","password":"secret12"}
            """.formatted(name, email, phone)))
        .andExpect(status().isCreated());
    return issueTokenFor(email);
  }

  private String issueTokenFor(String email) throws Exception {
    MvcResult tokenResponse = mockMvc.perform(post(ApiV1.path("/auth/tokens"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"email":"%s","password":"secret12"}
                """.formatted(email)))
        .andExpect(status().isCreated())
        .andReturn();
    return objectMapper.readTree(tokenResponse.getResponse().getContentAsString())
        .get("token")
        .asText();
  }

  private static String bearer(String token) {
    return "Bearer " + token;
  }
}
