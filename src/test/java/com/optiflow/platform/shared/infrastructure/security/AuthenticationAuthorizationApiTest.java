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
import com.optiflow.platform.clinical.ClinicalRecordTestData;
import com.optiflow.platform.clinical.SaleTestData;
import com.optiflow.platform.clinical.domain.repositories.ClinicalRecordRepository;
import com.optiflow.platform.clinical.domain.repositories.QuotationRepository;
import com.optiflow.platform.clinical.domain.repositories.SaleRepository;
import com.optiflow.platform.notification.domain.model.aggregates.Notification;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationMessage;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import com.optiflow.platform.notification.domain.repositories.NotificationRepository;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
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
  @Autowired private QuotationRepository quotationRepository;
  @Autowired private ClinicalRecordRepository clinicalRecordRepository;
  @Autowired private NotificationRepository notificationRepository;
  @Autowired private SaleRepository saleRepository;

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
        .andExpect(status().isForbidden());

    String securedPatientId = objectMapper.readTree(
            mockMvc.perform(post(ApiV1.path("/auth/tokens"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"email":"secured.patient@optiflow.test","password":"secret12"}
                        """))
                .andReturn().getResponse().getContentAsString())
        .get("userId").asText();
    String appointmentId = objectMapper.readTree(
            mockMvc.perform(post(ApiV1.path("/appointments"))
                    .header(HttpHeaders.AUTHORIZATION, bearer(patientToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"patientId":"%s","opticalStoreId":"%s","timeSlotId":"%s"}
                        """.formatted(securedPatientId, storeId, timeSlotId)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString())
        .get("id").asText();

    String otherToken = issueTokenFor("other.patient@optiflow.test");
    mockMvc.perform(get(ApiV1.path("/appointments/" + appointmentId))
            .header(HttpHeaders.AUTHORIZATION, bearer(otherToken)))
        .andExpect(status().isForbidden());
    mockMvc.perform(patch(ApiV1.path("/appointments/" + appointmentId + "/status"))
            .header(HttpHeaders.AUTHORIZATION, bearer(otherToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"status":"CANCELLED"}
                """))
        .andExpect(status().isForbidden());
    mockMvc.perform(get(ApiV1.path("/appointments/" + appointmentId))
            .header(HttpHeaders.AUTHORIZATION, bearer(patientToken)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CONFIRMED"));

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
  void letsPatientRateStoresOnlyInTheirOwnName() throws Exception {
    String raterToken = registerPatientAndIssueToken(
        "Rater Patient", "rater.patient@optiflow.test", "999444555");
    registerPatientAndIssueToken("Other Rater", "other.rater@optiflow.test", "999444556");
    String raterId = userIdOf("rater.patient@optiflow.test");
    String otherId = userIdOf("other.rater@optiflow.test");
    String rating = """
        {"patientId":"%s","score":4,"comment":"Good service"}
        """;

    mockMvc.perform(post(ApiV1.path("/optical-stores/22222222-2222-2222-2222-222222222222/ratings"))
            .header(HttpHeaders.AUTHORIZATION, bearer(raterToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content(rating.formatted(otherId)))
        .andExpect(status().isForbidden());
    mockMvc.perform(post(ApiV1.path("/optical-stores/22222222-2222-2222-2222-222222222222/ratings"))
            .header(HttpHeaders.AUTHORIZATION, bearer(raterToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content(rating.formatted(raterId)))
        .andExpect(status().isCreated());
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
  void answersBadRequestWhenARequiredParameterIsMissing() throws Exception {
    String adminToken = issueTokenFor("admin@optiflow.test");

    mockMvc.perform(get(ApiV1.path("/reports/sales"))
            .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("The parameter from is required."));
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
  void letsAdminDeactivateAndReactivateStaffMembers() throws Exception {
    String adminToken = issueTokenFor("admin@optiflow.test");
    String advisorToken = registerStaffAndIssueToken(
        adminToken, "Leaving Advisor", "leaving.advisor@optiflow.test", "SALES_ADVISOR");
    String advisorId = userIdOf("leaving.advisor@optiflow.test");
    String adminId = userIdOf("admin@optiflow.test");

    mockMvc.perform(patch(ApiV1.path("/staff-members/" + advisorId + "/status"))
            .header(HttpHeaders.AUTHORIZATION, bearer(advisorToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"active\":false}"))
        .andExpect(status().isForbidden());
    mockMvc.perform(patch(ApiV1.path("/staff-members/" + adminId + "/status"))
            .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"active\":false}"))
        .andExpect(status().isBadRequest());
    mockMvc.perform(patch(ApiV1.path("/staff-members/" + advisorId + "/status"))
            .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"active\":false}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active").value(false));
    mockMvc.perform(post(ApiV1.path("/auth/tokens"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"leaving.advisor@optiflow.test\",\"password\":\"secret12\"}"))
        .andExpect(status().isUnauthorized());
    mockMvc.perform(patch(ApiV1.path("/staff-members/" + advisorId + "/status"))
            .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"active\":true}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active").value(true));
    issueTokenFor("leaving.advisor@optiflow.test");
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
  void letsClinicalStaffReadThePatientAppointments() throws Exception {
    String adminToken = issueTokenFor("admin@optiflow.test");
    String optometristToken = registerStaffAndIssueToken(
        adminToken, "Agenda Optometrist", "agenda.optometrist@optiflow.test", "OPTOMETRIST");
    String advisorToken = registerStaffAndIssueToken(
        adminToken, "Agenda Advisor", "agenda.advisor@optiflow.test", "SALES_ADVISOR");
    String technicianToken = registerStaffAndIssueToken(
        adminToken, "Agenda Technician", "agenda.technician@optiflow.test", "TECHNICIAN");
    registerPatientAndIssueToken("Agenda Patient", "agenda.patient@optiflow.test", "999888111");
    String patientId = userIdOf("agenda.patient@optiflow.test");

    mockMvc.perform(get(ApiV1.path("/patients/" + patientId + "/appointments"))
            .header(HttpHeaders.AUTHORIZATION, bearer(optometristToken)))
        .andExpect(status().isOk());
    mockMvc.perform(get(ApiV1.path("/patients/" + patientId + "/appointments"))
            .header(HttpHeaders.AUTHORIZATION, bearer(advisorToken)))
        .andExpect(status().isOk());
    mockMvc.perform(get(ApiV1.path("/patients/" + patientId + "/appointments"))
            .header(HttpHeaders.AUTHORIZATION, bearer(technicianToken)))
        .andExpect(status().isForbidden());
    // The technician reads the patient only to show the name on the work order.
    mockMvc.perform(get(ApiV1.path("/patients/" + patientId))
            .header(HttpHeaders.AUTHORIZATION, bearer(technicianToken)))
        .andExpect(status().isOk());
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
    mockMvc.perform(post(ApiV1.path("/inventory/frames"))
            .header(HttpHeaders.AUTHORIZATION, bearer(advisorToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"sku\":\"ADV-001-BLK\",\"name\":\"Montura del asesor\",\"price\":150.00}"))
        .andExpect(status().isCreated());
    mockMvc.perform(patch(ApiV1.path("/inventory/frames/ADV-001-BLK/price"))
            .header(HttpHeaders.AUTHORIZATION, bearer(advisorToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"price\":160.00}"))
        .andExpect(status().isOk());
    mockMvc.perform(post(ApiV1.path("/inventory/frames"))
            .header(HttpHeaders.AUTHORIZATION, bearer(patientToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"sku\":\"PAT-001-BLK\",\"name\":\"Montura\",\"price\":150.00}"))
        .andExpect(status().isForbidden());
    mockMvc.perform(get(ApiV1.path("/inventory/alerts")).param("opticalStoreId", storeId)
            .header(HttpHeaders.AUTHORIZATION, bearer(advisorToken)))
        .andExpect(status().isForbidden());
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
    mockMvc.perform(get(ApiV1.path("/reports/inventory"))
            .param("opticalStoreId", storeId).param("from", "2030-01-01").param("to", "2030-01-31")
            .header(HttpHeaders.AUTHORIZATION, bearer(advisorToken)))
        .andExpect(status().isForbidden());
    mockMvc.perform(get(ApiV1.path("/reports/inventory"))
            .param("opticalStoreId", storeId).param("from", "2030-01-01").param("to", "2030-01-31")
            .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
        .andExpect(status().isOk());
    mockMvc.perform(get(ApiV1.path("/reports/sales"))
            .param("from", "2030-01-01").param("to", "2030-01-31")
            .header(HttpHeaders.AUTHORIZATION, bearer(advisorToken)))
        .andExpect(status().isForbidden());
    mockMvc.perform(get(ApiV1.path("/reports/sales"))
            .param("from", "2030-01-01").param("to", "2030-01-31")
            .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
        .andExpect(status().isOk());
    mockMvc.perform(get(ApiV1.path("/reports/production"))
            .param("from", "2030-01-01").param("to", "2030-01-31")
            .header(HttpHeaders.AUTHORIZATION, bearer(advisorToken)))
        .andExpect(status().isForbidden());
    mockMvc.perform(get(ApiV1.path("/reports/production"))
            .param("from", "2030-01-01").param("to", "2030-01-31")
            .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
        .andExpect(status().isOk());
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
    mockMvc.perform(get(ApiV1.path("/appointments"))
            .param("opticalStoreId", "11111111-1111-1111-1111-111111111111")
            .param("date", "2030-01-07")
            .header(HttpHeaders.AUTHORIZATION, bearer(advisorToken)))
        .andExpect(status().isOk());
    mockMvc.perform(get(ApiV1.path("/appointments/00000000-0000-0000-0000-000000000009"))
            .header(HttpHeaders.AUTHORIZATION, bearer(advisorToken)))
        .andExpect(status().isNotFound());
    mockMvc.perform(patch(ApiV1.path("/appointments/00000000-0000-0000-0000-000000000009/status"))
            .header(HttpHeaders.AUTHORIZATION, bearer(advisorToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"status":"CANCELLED"}
                """))
        .andExpect(status().isForbidden());
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

  @Test
  void letsOnlyThePatientOfASaleReadAndPayIt() throws Exception {
    String adminToken = issueTokenFor("admin@optiflow.test");
    String advisorToken = registerStaffAndIssueToken(
        adminToken, "Checkout Advisor", "checkout.advisor@optiflow.test", "SALES_ADVISOR");
    String ownerToken = registerPatientAndIssueToken(
        "Sale Owner", "sale.owner@optiflow.test", "999333111");
    String otherToken = registerPatientAndIssueToken(
        "Sale Stranger", "sale.stranger@optiflow.test", "999333112");
    UUID ownerId = UUID.fromString(objectMapper.readTree(mockMvc.perform(
            post(ApiV1.path("/auth/tokens")).contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"sale.owner@optiflow.test\",\"password\":\"secret12\"}"))
            .andReturn().getResponse().getContentAsString())
        .get("userId").asText());
    String sale = ApiV1.path("/sales/"
        + SaleTestData.pendingSale(quotationRepository, saleRepository, ownerId));
    String preference = sale + "/payment-preference";

    mockMvc.perform(get(sale).header(HttpHeaders.AUTHORIZATION, bearer(otherToken)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("You cannot access another patient's data."));
    mockMvc.perform(get(sale).header(HttpHeaders.AUTHORIZATION, bearer(ownerToken)))
        .andExpect(status().isOk());
    mockMvc.perform(get(sale).header(HttpHeaders.AUTHORIZATION, bearer(advisorToken)))
        .andExpect(status().isOk());
    mockMvc.perform(get(ApiV1.path("/patients/" + ownerId + "/sales"))
            .header(HttpHeaders.AUTHORIZATION, bearer(otherToken)))
        .andExpect(status().isForbidden());
    mockMvc.perform(get(ApiV1.path("/patients/" + ownerId + "/sales"))
            .header(HttpHeaders.AUTHORIZATION, bearer(ownerToken)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].status").value("PENDING_PAYMENT"));

    mockMvc.perform(post(preference).header(HttpHeaders.AUTHORIZATION, bearer(advisorToken)))
        .andExpect(status().isForbidden());
    mockMvc.perform(post(preference).header(HttpHeaders.AUTHORIZATION, bearer(otherToken)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("You cannot pay another patient's sale."));
    // The owner passes the checks; there is no Mercado Pago access token in the tests.
    mockMvc.perform(post(preference).header(HttpHeaders.AUTHORIZATION, bearer(ownerToken)))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.message").value("The payment gateway is not configured."));
  }

  @Test
  void letsOnlyThePatientOfAClinicalRecordReadItAndItsQuotations() throws Exception {
    String adminToken = issueTokenFor("admin@optiflow.test");
    String advisorToken = registerStaffAndIssueToken(
        adminToken, "Record Advisor", "record.advisor@optiflow.test", "SALES_ADVISOR");
    String ownerToken = registerPatientAndIssueToken(
        "Record Owner", "record.owner@optiflow.test", "999444111");
    String otherToken = registerPatientAndIssueToken(
        "Record Stranger", "record.stranger@optiflow.test", "999444112");
    UUID ownerId = UUID.fromString(objectMapper.readTree(mockMvc.perform(
            post(ApiV1.path("/auth/tokens")).contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"record.owner@optiflow.test\",\"password\":\"secret12\"}"))
            .andReturn().getResponse().getContentAsString())
        .get("userId").asText());
    ClinicalRecordTestData.SavedRecord saved = ClinicalRecordTestData.withQuotation(
        clinicalRecordRepository, quotationRepository, ownerId);

    for (String route : List.of(
        "/clinical-records/" + saved.clinicalRecordId(),
        "/clinical-records/" + saved.clinicalRecordId() + "/prescription",
        "/quotations/" + saved.quotationId())) {
      mockMvc.perform(get(ApiV1.path(route)).header(HttpHeaders.AUTHORIZATION, bearer(otherToken)))
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.message").value("You cannot access another patient's data."));
      mockMvc.perform(get(ApiV1.path(route)).header(HttpHeaders.AUTHORIZATION, bearer(ownerToken)))
          .andExpect(status().isOk());
      mockMvc.perform(get(ApiV1.path(route))
              .header(HttpHeaders.AUTHORIZATION, bearer(advisorToken)))
          .andExpect(status().isOk());
    }
  }

  @Test
  void letsOnlyThePatientReadTheirNotificationsAndLoyaltyAccount() throws Exception {
    String adminToken = issueTokenFor("admin@optiflow.test");
    String advisorToken = registerStaffAndIssueToken(
        adminToken, "Notice Advisor", "notice.advisor@optiflow.test", "SALES_ADVISOR");
    String ownerToken = registerPatientAndIssueToken(
        "Notice Owner", "notice.owner@optiflow.test", "999555111");
    String otherToken = registerPatientAndIssueToken(
        "Notice Stranger", "notice.stranger@optiflow.test", "999555112");
    UUID ownerId = UUID.fromString(objectMapper.readTree(mockMvc.perform(
            post(ApiV1.path("/auth/tokens")).contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"notice.owner@optiflow.test\",\"password\":\"secret12\"}"))
            .andReturn().getResponse().getContentAsString())
        .get("userId").asText());
    Notification notification = Notification.sendBirthdayDiscount(
        NotificationId.generate(), PatientId.of(ownerId),
        new NotificationMessage("¡Feliz cumpleaños!", "Sumamos 100 puntos a tu cuenta."),
        Instant.now());
    notificationRepository.save(notification);
    String notice = ApiV1.path("/notifications/" + notification.id().value());
    String notices = ApiV1.path("/patients/" + ownerId + "/notifications");

    for (String route : List.of(notice, notices)) {
      mockMvc.perform(get(route).header(HttpHeaders.AUTHORIZATION, bearer(otherToken)))
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.message").value("You cannot access another patient's data."));
      mockMvc.perform(get(route).header(HttpHeaders.AUTHORIZATION, bearer(advisorToken)))
          .andExpect(status().isForbidden());
      mockMvc.perform(get(route).header(HttpHeaders.AUTHORIZATION, bearer(ownerToken)))
          .andExpect(status().isOk());
    }
    mockMvc.perform(patch(notice).header(HttpHeaders.AUTHORIZATION, bearer(otherToken))
            .contentType(MediaType.APPLICATION_JSON).content("{\"read\":true}"))
        .andExpect(status().isForbidden());
    mockMvc.perform(get(ApiV1.path("/patients/" + ownerId + "/loyalty-account"))
            .header(HttpHeaders.AUTHORIZATION, bearer(otherToken)))
        .andExpect(status().isForbidden());
    mockMvc.perform(post(ApiV1.path("/patients/" + ownerId + "/device-tokens"))
            .header(HttpHeaders.AUTHORIZATION, bearer(otherToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"token\":\"fcm-stranger\",\"platform\":\"ANDROID\"}"))
        .andExpect(status().isForbidden());
    mockMvc.perform(post(ApiV1.path("/patients/" + ownerId + "/device-tokens"))
            .header(HttpHeaders.AUTHORIZATION, bearer(ownerToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"token\":\"fcm-owner\",\"platform\":\"ANDROID\"}"))
        .andExpect(status().isCreated());
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

  private String userIdOf(String email) throws Exception {
    return objectMapper.readTree(
            mockMvc.perform(post(ApiV1.path("/auth/tokens"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"email":"%s","password":"secret12"}
                        """.formatted(email)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString())
        .get("userId").asText();
  }

  private static String bearer(String token) {
    return "Bearer " + token;
  }
}
