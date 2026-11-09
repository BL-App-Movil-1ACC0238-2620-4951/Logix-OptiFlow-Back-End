package com.optiflow.platform.shared.infrastructure.security;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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

    mockMvc.perform(get(ApiV1.path("/staff-members"))
            .header(HttpHeaders.AUTHORIZATION, bearer(patientToken)))
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

    mockMvc.perform(get(ApiV1.path("/staff-members"))
            .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].role", hasItem("ADMIN")));
  }

  @Test
  void allowsAdminToPublishTimeSlotsAndReturnsTheirLocation() throws Exception {
    String adminToken = issueTokenFor("admin@optiflow.test");
    MvcResult stores = mockMvc.perform(get(ApiV1.path("/optical-stores")))
        .andExpect(status().isOk())
        .andReturn();
    String storeId = objectMapper.readTree(stores.getResponse().getContentAsString())
        .get(0).get("id").asText();

    mockMvc.perform(post(ApiV1.path("/optical-stores/" + storeId + "/time-slots"))
            .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"timeSlots":[{"startDateTime":"2030-01-15T14:00:00Z",
                           "endDateTime":"2030-01-15T14:30:00Z"}]}
                """))
        .andExpect(status().isCreated())
        .andExpect(header().string(HttpHeaders.LOCATION,
            endsWith(ApiV1.path("/optical-stores/" + storeId + "/time-slots"))))
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  void forbidsPatientFromStaffOperations() throws Exception {
    String patientToken = registerPatientAndIssueToken(
        "Curious Patient", "curious.patient@optiflow.test", "999444555");
    String anyId = "00000000-0000-0000-0000-000000000009";

    mockMvc.perform(post(ApiV1.path("/clinical-records"))
            .header(HttpHeaders.AUTHORIZATION, bearer(patientToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isForbidden());
    mockMvc.perform(patch(ApiV1.path("/sales/" + anyId + "/status"))
            .header(HttpHeaders.AUTHORIZATION, bearer(patientToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"status\":\"CLOSED\"}"))
        .andExpect(status().isForbidden());
    mockMvc.perform(post(ApiV1.path("/work-orders"))
            .header(HttpHeaders.AUTHORIZATION, bearer(patientToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isForbidden());
  }

  @Test
  void appliesTheRolesOfEachStaffMember() throws Exception {
    String adminToken = issueTokenFor("admin@optiflow.test");
    String technicianToken = registerStaffAndIssueToken(
        adminToken, "Tech Staff", "tech.staff@optiflow.test", "TECHNICIAN");
    String advisorToken = registerStaffAndIssueToken(
        adminToken, "Advisor Staff", "advisor.staff@optiflow.test", "SALES_ADVISOR");
    String anyId = "00000000-0000-0000-0000-000000000009";

    mockMvc.perform(post(ApiV1.path("/quotations"))
            .header(HttpHeaders.AUTHORIZATION, bearer(technicianToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isForbidden());
    mockMvc.perform(get(ApiV1.path("/patients/" + anyId + "/work-orders"))
            .header(HttpHeaders.AUTHORIZATION, bearer(technicianToken)))
        .andExpect(status().isOk());
    mockMvc.perform(get(ApiV1.path("/clinical-records/" + anyId))
            .header(HttpHeaders.AUTHORIZATION, bearer(advisorToken)))
        .andExpect(status().isNotFound());
    mockMvc.perform(get(ApiV1.path("/technicians"))
            .header(HttpHeaders.AUTHORIZATION, bearer(advisorToken)))
        .andExpect(status().isForbidden());
    mockMvc.perform(get(ApiV1.path("/work-orders"))
            .header(HttpHeaders.AUTHORIZATION, bearer(advisorToken)))
        .andExpect(status().isOk());
    mockMvc.perform(patch(ApiV1.path("/work-orders/" + anyId + "/status"))
            .header(HttpHeaders.AUTHORIZATION, bearer(advisorToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"status\":\"DELIVERED\"}"))
        .andExpect(status().isForbidden());
  }

  @Test
  void letsOnlyClinicalStaffReadTheClinicalRecordsSummary() throws Exception {
    String adminToken = issueTokenFor("admin@optiflow.test");
    String optometristToken = registerStaffAndIssueToken(
        adminToken, "Summary Optometrist", "summary.optometrist@optiflow.test", "OPTOMETRIST");
    String technicianToken = registerStaffAndIssueToken(
        adminToken, "Summary Technician", "summary.technician@optiflow.test", "TECHNICIAN");
    String patientToken = registerPatientAndIssueToken(
        "Summary Patient", "summary.patient@optiflow.test", "999777888");

    mockMvc.perform(get(ApiV1.path("/clinical-records/summary"))
            .header(HttpHeaders.AUTHORIZATION, bearer(optometristToken)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.patientsWithRecords").isNumber());
    mockMvc.perform(get(ApiV1.path("/clinical-records/summary"))
            .header(HttpHeaders.AUTHORIZATION, bearer(technicianToken)))
        .andExpect(status().isForbidden());
    mockMvc.perform(get(ApiV1.path("/clinical-records/summary"))
            .header(HttpHeaders.AUTHORIZATION, bearer(patientToken)))
        .andExpect(status().isForbidden());
  }

  @Test
  void appliesTheInventoryRolesOfTheReport() throws Exception {
    String adminToken = issueTokenFor("admin@optiflow.test");
    String advisorToken = registerStaffAndIssueToken(
        adminToken, "Inventory Advisor", "inventory.advisor@optiflow.test", "SALES_ADVISOR");
    String patientToken = registerPatientAndIssueToken(
        "Catalog Patient", "catalog.patient@optiflow.test", "999888111");
    String storeId = "11111111-1111-1111-1111-111111111111";

    mockMvc.perform(get(ApiV1.path("/inventory/frames"))
            .header(HttpHeaders.AUTHORIZATION, bearer(patientToken)))
        .andExpect(status().isOk());
    mockMvc.perform(get(ApiV1.path("/inventory/stock")).param("opticalStoreId", storeId)
            .header(HttpHeaders.AUTHORIZATION, bearer(patientToken)))
        .andExpect(status().isForbidden());
    mockMvc.perform(get(ApiV1.path("/inventory/stock")).param("opticalStoreId", storeId)
            .header(HttpHeaders.AUTHORIZATION, bearer(advisorToken)))
        .andExpect(status().isOk());
    mockMvc.perform(post(ApiV1.path("/suppliers"))
            .header(HttpHeaders.AUTHORIZATION, bearer(advisorToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Proveedor\"}"))
        .andExpect(status().isForbidden());
    mockMvc.perform(post(ApiV1.path("/suppliers"))
            .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Proveedor\"}"))
        .andExpect(status().isCreated());
  }

  @Test
  void forbidsPatientFromOtherPatientsRecordsOrdersAndFavorites() throws Exception {
    String patientToken = registerPatientAndIssueToken(
        "Nosy Patient", "nosy.patient@optiflow.test", "999555666");
    String otherPatientId = "00000000-0000-0000-0000-000000000009";

    mockMvc.perform(get(ApiV1.path("/patients/" + otherPatientId + "/clinical-records"))
            .header(HttpHeaders.AUTHORIZATION, bearer(patientToken)))
        .andExpect(status().isForbidden());
    mockMvc.perform(get(ApiV1.path("/patients/" + otherPatientId + "/work-orders"))
            .header(HttpHeaders.AUTHORIZATION, bearer(patientToken)))
        .andExpect(status().isForbidden());
    mockMvc.perform(post(ApiV1.path("/patients/" + otherPatientId + "/favorites"))
            .header(HttpHeaders.AUTHORIZATION, bearer(patientToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"opticalStoreId\":\"11111111-1111-1111-1111-111111111111\"}"))
        .andExpect(status().isForbidden());
  }

  @Test
  void letsPatientReadOnlyTheirOwnProfile() throws Exception {
    String patientToken = registerPatientAndIssueToken(
        "Profile Patient", "profile.patient@optiflow.test", "999666777");
    String patientId = objectMapper.readTree(
            mockMvc.perform(post(ApiV1.path("/auth/tokens"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"email":"profile.patient@optiflow.test","password":"secret12"}
                        """))
                .andReturn().getResponse().getContentAsString())
        .get("userId").asText();

    mockMvc.perform(get(ApiV1.path("/patients/" + patientId))
            .header(HttpHeaders.AUTHORIZATION, bearer(patientToken)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Profile Patient"))
        .andExpect(jsonPath("$.email").value("profile.patient@optiflow.test"));
    mockMvc.perform(get(ApiV1.path("/patients/00000000-0000-0000-0000-000000000009"))
            .header(HttpHeaders.AUTHORIZATION, bearer(patientToken)))
        .andExpect(status().isForbidden());
    mockMvc.perform(get(ApiV1.path("/patients"))
            .header(HttpHeaders.AUTHORIZATION, bearer(patientToken)))
        .andExpect(status().isForbidden());
  }

  @Test
  void letsStaffSearchPatientsAndBookForThem() throws Exception {
    registerPatientAndIssueToken(
        "Directory Patient", "directory.patient@optiflow.test", "999777888");
    String adminToken = issueTokenFor("admin@optiflow.test");
    String advisorToken = registerStaffAndIssueToken(
        adminToken, "Front Desk", "front.desk@optiflow.test", "SALES_ADVISOR");

    mockMvc.perform(get(ApiV1.path("/patients"))
            .param("name", "directory")
            .header(HttpHeaders.AUTHORIZATION, bearer(advisorToken)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].email").value("directory.patient@optiflow.test"));
    mockMvc.perform(post(ApiV1.path("/appointments"))
            .header(HttpHeaders.AUTHORIZATION, bearer(advisorToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void letsStaffMemberReadOnlyTheirOwnProfile() throws Exception {
    String adminToken = issueTokenFor("admin@optiflow.test");
    String optometristToken = registerStaffAndIssueToken(
        adminToken, "Own Profile", "own.profile@optiflow.test", "OPTOMETRIST");
    String optometristId = objectMapper.readTree(
            mockMvc.perform(post(ApiV1.path("/auth/tokens"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"email":"own.profile@optiflow.test","password":"secret12"}
                        """))
                .andReturn().getResponse().getContentAsString())
        .get("userId").asText();

    mockMvc.perform(get(ApiV1.path("/staff-members/" + optometristId))
            .header(HttpHeaders.AUTHORIZATION, bearer(optometristToken)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.role").value("OPTOMETRIST"))
        .andExpect(jsonPath("$.opticalStoreId").isNotEmpty());
    mockMvc.perform(get(ApiV1.path("/staff-members/00000000-0000-0000-0000-000000000009"))
            .header(HttpHeaders.AUTHORIZATION, bearer(optometristToken)))
        .andExpect(status().isForbidden());
    mockMvc.perform(get(ApiV1.path("/staff-members/" + optometristId))
            .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
        .andExpect(status().isOk());
  }

  private String registerStaffAndIssueToken(
      String adminToken, String name, String email, String role) throws Exception {
    String storeId = objectMapper.readTree(
            mockMvc.perform(get(ApiV1.path("/optical-stores")))
                .andReturn().getResponse().getContentAsString())
        .get(0).get("id").asText();
    mockMvc.perform(post(ApiV1.path("/staff-members"))
            .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"name":"%s","email":"%s","password":"secret12","opticalStoreId":"%s","role":"%s"}
                """.formatted(name, email, storeId, role)))
        .andExpect(status().isCreated());
    return issueTokenFor(email);
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
