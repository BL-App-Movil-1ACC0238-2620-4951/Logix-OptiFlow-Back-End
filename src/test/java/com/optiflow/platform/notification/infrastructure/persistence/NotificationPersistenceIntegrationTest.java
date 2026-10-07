package com.optiflow.platform.notification.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.optiflow.platform.notification.application.queries.GetNotificationsByPatientQuery;
import com.optiflow.platform.notification.application.services.NotificationApplicationService;
import com.optiflow.platform.notification.domain.valueobjects.NotificationType;
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
class NotificationPersistenceIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private NotificationApplicationService notificationApplicationService;

  @Test
  void persistsAppointmentReminderWhenAnAppointmentIsBooked() throws Exception {
    MvcResult stores = mockMvc.perform(get("/optical-stores"))
        .andExpect(status().isOk())
        .andReturn();
    String storeId = objectMapper.readTree(stores.getResponse().getContentAsString())
        .get("opticalStores").get(0).get("id").asText();

    MvcResult availability = mockMvc.perform(get("/optical-stores/" + storeId + "/availability"))
        .andExpect(status().isOk())
        .andReturn();
    String timeSlotId = objectMapper.readTree(availability.getResponse().getContentAsString())
        .get("timeSlots").get(0).get("id").asText();

    MvcResult patient = mockMvc.perform(post("/patients")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"name":"Nora Vega","email":"nora.vega@optiflow.test","phone":"999777666","password":"secret12"}
                """))
        .andExpect(status().isCreated())
        .andReturn();
    String patientId = objectMapper.readTree(patient.getResponse().getContentAsString())
        .get("id").asText();

    mockMvc.perform(post("/appointments")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"patientId":"%s","opticalStoreId":"%s","timeSlotId":"%s"}
                """.formatted(patientId, storeId, timeSlotId)))
        .andExpect(status().isCreated());

    var notifications = notificationApplicationService.findByPatient(
        new GetNotificationsByPatientQuery(java.util.UUID.fromString(patientId)));

    assertEquals(1, notifications.size());
    assertEquals(NotificationType.APPOINTMENT_REMINDER, notifications.get(0).type());
  }
}
