package com.optiflow.platform.notification.domain.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.optiflow.platform.notification.domain.valueobjects.AppointmentId;
import com.optiflow.platform.notification.domain.valueobjects.LoyaltyPoints;
import com.optiflow.platform.notification.domain.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.valueobjects.NotificationMessage;
import com.optiflow.platform.notification.domain.valueobjects.NotificationStatus;
import com.optiflow.platform.notification.domain.valueobjects.NotificationType;
import com.optiflow.platform.notification.domain.valueobjects.PatientId;
import com.optiflow.platform.notification.domain.valueobjects.WorkOrderId;
import com.optiflow.platform.shared.exceptions.DomainException;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class NotificationAndLoyaltyRulesTest {

  private static final Instant NOW = Instant.parse("2026-10-07T15:00:00Z");
  private static final PatientId PATIENT_ID = PatientId.of(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));
  private static final AppointmentId APPOINTMENT_ID =
      AppointmentId.of(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"));
  private static final WorkOrderId WORK_ORDER_ID =
      WorkOrderId.of(UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"));

  @Test
  void schedulesAnUnreadAppointmentReminder() {
    Notification notification = Notification.scheduleAppointmentReminder(
        NotificationId.generate(),
        PATIENT_ID,
        APPOINTMENT_ID,
        new NotificationMessage("Appointment confirmed", "Your visit is scheduled."),
        NOW);

    assertEquals(NotificationType.APPOINTMENT_REMINDER, notification.type());
    assertEquals(NotificationStatus.UNREAD, notification.status());
    assertTrue(notification.appointmentId().isPresent());
    assertEquals(APPOINTMENT_ID, notification.appointmentId().orElseThrow());
  }

  @Test
  void marksANotificationAsReadOnce() {
    Notification notification = Notification.notifyLensOrderProgress(
        NotificationId.generate(),
        PATIENT_ID,
        WORK_ORDER_ID,
        new NotificationMessage("Lens update", "Your lenses are in quality control."),
        NOW);

    notification.markAsRead(NOW.plusSeconds(30));

    assertEquals(NotificationStatus.READ, notification.status());
    assertTrue(notification.readAt().isPresent());

    DomainException exception =
        assertThrows(DomainException.class, () -> notification.markAsRead(NOW.plusSeconds(60)));
    assertEquals(409, exception.status());
  }

  @Test
  void grantsBirthdayBonusPointsOnlyOncePerYear() {
    LoyaltyAccount account = LoyaltyAccount.open(PATIENT_ID, NOW);

    LoyaltyPoints bonus = account.grantBirthdayDiscount(2026, NOW);

    assertEquals(LoyaltyAccount.BIRTHDAY_BONUS_POINTS, bonus);
    assertEquals(LoyaltyPoints.of(100), account.balance());

    DomainException exception =
        assertThrows(DomainException.class, () -> account.grantBirthdayDiscount(2026, NOW));
    assertEquals(409, exception.status());
  }
}
