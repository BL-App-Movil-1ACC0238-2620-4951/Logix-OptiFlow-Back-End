package com.optiflow.platform.notification.application.eventhandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.optiflow.platform.notification.application.commandhandlers.ScheduleAppointmentReminderHandler;
import com.optiflow.platform.notification.application.commandhandlers.SendPushNotificationHandler;
import com.optiflow.platform.notification.application.outboundservices.PushDelivery;
import com.optiflow.platform.notification.domain.model.aggregates.Notification;
import com.optiflow.platform.notification.domain.model.commands.ScheduleAppointmentReminderCommand;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationType;
import com.optiflow.platform.notification.domain.model.valueobjects.PushStatus;
import com.optiflow.platform.notification.support.FakeDeviceTokenRepository;
import com.optiflow.platform.notification.support.FakeNotificationRepository;
import com.optiflow.platform.notification.support.FakePatientDirectory;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ScheduledNotificationDispatcherTest {

  private static final Instant BOOKED_AT = Instant.parse("2026-11-08T15:00:00Z");
  private static final Instant APPOINTMENT_AT = Instant.parse("2026-11-12T15:00:00Z");
  private static final Instant DISPATCHED_AT = Instant.parse("2026-11-11T15:05:00Z");
  private static final UUID PATIENT_ID = UUID.randomUUID();

  private final FakeNotificationRepository notificationRepository =
      new FakeNotificationRepository();
  private final FakePatientDirectory patientDirectory = new FakePatientDirectory();
  private final DomainEventPublisher eventPublisher = Mockito.mock(DomainEventPublisher.class);

  @Test
  void sendsTheDueReminderAndKeepsTheAnnualCheckupForLater() {
    book();

    dispatcher().dispatch();

    assertEquals(PushStatus.FAILED, reminder(NotificationType.APPOINTMENT_REMINDER).pushStatus());
    assertEquals(PushStatus.PENDING,
        reminder(NotificationType.ANNUAL_CHECKUP_REMINDER).pushStatus());
    assertEquals(2, notificationRepository.findAll().size());
  }

  @Test
  void deletesTheRemindersOfACancelledAppointment() {
    UUID appointmentId = book();
    patientDirectory.cancel(appointmentId);

    dispatcher().dispatch();

    assertTrue(notificationRepository.findAll().isEmpty());
  }

  private UUID book() {
    UUID appointmentId = UUID.randomUUID();
    patientDirectory.book(appointmentId, PATIENT_ID, APPOINTMENT_AT);
    new ScheduleAppointmentReminderHandler(notificationRepository, patientDirectory,
        eventPublisher, Clock.fixed(BOOKED_AT, ZoneOffset.UTC))
        .handle(new ScheduleAppointmentReminderCommand(PATIENT_ID, appointmentId));
    return appointmentId;
  }

  private Notification reminder(NotificationType type) {
    return notificationRepository.findAll().stream()
        .filter(notification -> notification.type() == type)
        .findFirst()
        .orElseThrow();
  }

  private ScheduledNotificationDispatcher dispatcher() {
    Clock clock = Clock.fixed(DISPATCHED_AT, ZoneOffset.UTC);
    SendPushNotificationHandler sendPushNotificationHandler = new SendPushNotificationHandler(
        notificationRepository, new FakeDeviceTokenRepository(),
        (notification, devices) -> PushDelivery.NONE, eventPublisher, clock);
    return new ScheduledNotificationDispatcher(
        notificationRepository, patientDirectory, sendPushNotificationHandler, clock);
  }
}
