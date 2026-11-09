package com.optiflow.platform.notification.interfaces.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.optiflow.platform.notification.domain.model.aggregates.LoyaltyAccount;
import com.optiflow.platform.notification.domain.model.aggregates.Notification;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationMessage;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import com.optiflow.platform.notification.domain.model.valueobjects.WorkOrderId;
import com.optiflow.platform.notification.domain.repositories.DeviceTokenRepository;
import com.optiflow.platform.notification.domain.repositories.LoyaltyAccountRepository;
import com.optiflow.platform.notification.domain.repositories.NotificationRepository;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationApiTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private NotificationRepository notificationRepository;

  @Autowired
  private LoyaltyAccountRepository loyaltyAccountRepository;

  @Autowired
  private DeviceTokenRepository deviceTokenRepository;

  @Test
  void listsAndMarksTheNotificationsOfAPatientAsRead() throws Exception {
    UUID patientId = UUID.randomUUID();
    Notification notification = Notification.notifyLensOrderProgress(
        NotificationId.generate(), PatientId.of(patientId), WorkOrderId.of(UUID.randomUUID()),
        new NotificationMessage("Avance de tu pedido", "Tus lentes están en control de calidad."),
        Instant.now());
    notificationRepository.save(notification);
    String route = ApiV1.path("/notifications/" + notification.id().value());

    mockMvc.perform(get(ApiV1.path("/patients/" + patientId + "/notifications")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].type").value("LENS_ORDER_PROGRESS"))
        .andExpect(jsonPath("$[0].status").value("UNREAD"));
    mockMvc.perform(get(route))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Avance de tu pedido"));
    mockMvc.perform(markAsRead(route, false))
        .andExpect(status().isBadRequest());
    mockMvc.perform(markAsRead(route, true))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("READ"))
        .andExpect(jsonPath("$.readAt").isNotEmpty());
    mockMvc.perform(markAsRead(route, true))
        .andExpect(status().isConflict());
    mockMvc.perform(get(ApiV1.path("/notifications/" + UUID.randomUUID())))
        .andExpect(status().isNotFound());
  }

  private static MockHttpServletRequestBuilder markAsRead(String route, boolean read) {
    return patch(route).contentType(MediaType.APPLICATION_JSON).content("{\"read\":" + read + "}");
  }

  @Test
  void answersASatisfactionSurveyOnce() throws Exception {
    Notification survey = Notification.sendSatisfactionSurvey(
        NotificationId.generate(), PatientId.of(UUID.randomUUID()),
        WorkOrderId.of(UUID.randomUUID()),
        new NotificationMessage("Encuesta de satisfacción", "Califícanos del 1 al 5."),
        Instant.now());
    notificationRepository.save(survey);
    String route = ApiV1.path("/notifications/" + survey.id().value() + "/survey-response");

    mockMvc.perform(post(route).contentType(MediaType.APPLICATION_JSON).content("{\"rating\":0}"))
        .andExpect(status().isBadRequest());
    mockMvc.perform(post(route).contentType(MediaType.APPLICATION_JSON)
            .content("{\"rating\":5,\"comment\":\"Excelente\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.surveyRating").value(5))
        .andExpect(jsonPath("$.surveyComment").value("Excelente"))
        .andExpect(jsonPath("$.status").value("READ"));
    mockMvc.perform(post(route).contentType(MediaType.APPLICATION_JSON).content("{\"rating\":4}"))
        .andExpect(status().isConflict());
  }

  @Test
  void registersADeviceTokenAndMovesItToTheLastPatient() throws Exception {
    UUID firstPatient = UUID.randomUUID();
    UUID secondPatient = UUID.randomUUID();
    String token = "fcm-" + UUID.randomUUID();
    String body = "{\"token\":\"" + token + "\",\"platform\":\"ANDROID\"}";

    mockMvc.perform(post(ApiV1.path("/patients/" + firstPatient + "/device-tokens"))
            .contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.platform").value("ANDROID"));
    mockMvc.perform(post(ApiV1.path("/patients/" + secondPatient + "/device-tokens"))
            .contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isCreated());
    mockMvc.perform(post(ApiV1.path("/patients/" + secondPatient + "/device-tokens"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"token\":\"" + token + "\",\"platform\":\"IOS\"}"))
        .andExpect(status().isBadRequest());

    assertEquals(0, deviceTokenRepository.findByPatientId(PatientId.of(firstPatient)).size());
    assertEquals(1, deviceTokenRepository.findByPatientId(PatientId.of(secondPatient)).size());
  }

  @Test
  void returnsTheLoyaltyAccountOnceItIsOpened() throws Exception {
    UUID patientId = UUID.randomUUID();
    String route = ApiV1.path("/patients/" + patientId + "/loyalty-account");

    mockMvc.perform(get(route)).andExpect(status().isNotFound());

    LoyaltyAccount account = LoyaltyAccount.open(PatientId.of(patientId), Instant.now());
    account.grantBirthdayDiscount(2026, Instant.now());
    loyaltyAccountRepository.save(account);

    mockMvc.perform(get(route))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.balance").value(100))
        .andExpect(jsonPath("$.lastBirthdayDiscountYear").value(2026));
  }
}
