package com.optiflow.platform.notification.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.optiflow.platform.notification.domain.model.aggregates.Notification;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationType;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import com.optiflow.platform.notification.domain.repositories.NotificationRepository;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import java.util.Set;
import java.util.stream.Collectors;
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
  private NotificationRepository notificationRepository;

  @Test
  void persistsAppointmentReminderWhenAnAppointmentIsBooked() throws Exception {
    MvcResult stores = mockMvc.perform(get(ApiV1.path("/optical-stores")))
        .andExpect(status().isOk())
        .andReturn();
    String storeId = objectMapper.readTree(stores.getResponse().getContentAsString())
        .get(0).get("id").asText();

    MvcResult availability = mockMvc.perform(get(ApiV1.path("/optical-stores/" + storeId + "/time-slots")))
        .andExpect(status().isOk())
        .andReturn();
    String timeSlotId = objectMapper.readTree(availability.getResponse().getContentAsString())
        .get(0).get("id").asText();

    MvcResult patient = mockMvc.perform(post(ApiV1.path("/patients"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"name":"Nora Vega","email":"nora.vega@optiflow.test","phone":"999777666","password":"secret12"}
                """))
        .andExpect(status().isCreated())
        .andReturn();
    String patientId = objectMapper.readTree(patient.getResponse().getContentAsString())
        .get("id").asText();

    mockMvc.perform(post(ApiV1.path("/appointments"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"patientId":"%s","opticalStoreId":"%s","timeSlotId":"%s"}
                """.formatted(patientId, storeId, timeSlotId)))
        .andExpect(status().isCreated());

    var notifications = notificationRepository.findByPatientId(
        PatientId.of(java.util.UUID.fromString(patientId)));

    assertEquals(
        Set.of(NotificationType.APPOINTMENT_REMINDER, NotificationType.ANNUAL_CHECKUP_REMINDER),
        notifications.stream().map(Notification::type).collect(Collectors.toSet()));
    assertTrue(notifications.stream().allMatch(n -> n.scheduledAt().isPresent()));
  }
}
