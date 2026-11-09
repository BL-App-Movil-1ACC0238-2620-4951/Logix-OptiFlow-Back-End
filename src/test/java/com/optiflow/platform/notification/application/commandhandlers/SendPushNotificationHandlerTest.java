package com.optiflow.platform.notification.application.commandhandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.optiflow.platform.notification.application.outboundservices.PushDelivery;
import com.optiflow.platform.notification.domain.model.aggregates.Notification;
import com.optiflow.platform.notification.domain.model.commands.SendPushNotificationCommand;
import com.optiflow.platform.notification.domain.model.entities.DeviceToken;
import com.optiflow.platform.notification.domain.model.valueobjects.DevicePlatform;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationMessage;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import com.optiflow.platform.notification.domain.model.valueobjects.PushStatus;
import com.optiflow.platform.notification.domain.model.valueobjects.WorkOrderId;
import com.optiflow.platform.notification.support.FakeDeviceTokenRepository;
import com.optiflow.platform.notification.support.FakeNotificationRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SendPushNotificationHandlerTest {

  private static final Instant NOW = Instant.parse("2026-11-08T15:00:00Z");
  private static final PatientId PATIENT = PatientId.of(UUID.randomUUID());

  private final FakeNotificationRepository notificationRepository =
      new FakeNotificationRepository();
  private final FakeDeviceTokenRepository deviceTokenRepository = new FakeDeviceTokenRepository();

  @Test
  void marksTheNotificationAsFailedWhenThePatientHasNoDevice() {
    Notification notification = savedNotification();

    handler(new PushDelivery(1, List.of())).handle(command(notification));

    assertEquals(PushStatus.FAILED, notification.pushStatus());
  }

  @Test
  void marksTheNotificationAsSentAndRemovesTheRejectedTokens() {
    Notification notification = savedNotification();
    deviceTokenRepository.save(DeviceToken.register(PATIENT, "phone", DevicePlatform.ANDROID, NOW));
    deviceTokenRepository.save(DeviceToken.register(PATIENT, "old", DevicePlatform.ANDROID, NOW));

    handler(new PushDelivery(1, List.of("old"))).handle(command(notification));

    assertEquals(PushStatus.SENT, notification.pushStatus());
    assertEquals(1, deviceTokenRepository.findByPatientId(PATIENT).size());
    assertTrue(deviceTokenRepository.findByToken("phone").isPresent());
  }

  @Test
  void marksTheNotificationAsFailedWhenNoDeviceReceivesIt() {
    Notification notification = savedNotification();
    deviceTokenRepository.save(DeviceToken.register(PATIENT, "phone", DevicePlatform.ANDROID, NOW));

    handler(PushDelivery.NONE).handle(command(notification));

    assertEquals(PushStatus.FAILED, notification.pushStatus());
  }

  private Notification savedNotification() {
    Notification notification = Notification.notifyLensOrderProgress(
        NotificationId.generate(), PATIENT, WorkOrderId.of(UUID.randomUUID()),
        new NotificationMessage("Avance de tu pedido", "Tu pedido fue entregado."), NOW);
    notificationRepository.save(notification);
    return notification;
  }

  private static SendPushNotificationCommand command(Notification notification) {
    return new SendPushNotificationCommand(notification.id().value());
  }

  private SendPushNotificationHandler handler(PushDelivery delivery) {
    return new SendPushNotificationHandler(notificationRepository, deviceTokenRepository,
        (notification, devices) -> delivery, Mockito.mock(DomainEventPublisher.class),
        Clock.fixed(NOW, ZoneOffset.UTC));
  }
}
