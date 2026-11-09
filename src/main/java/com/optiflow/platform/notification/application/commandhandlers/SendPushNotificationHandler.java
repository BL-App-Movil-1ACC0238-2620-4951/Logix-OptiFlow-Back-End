package com.optiflow.platform.notification.application.commandhandlers;

import com.optiflow.platform.notification.application.outboundservices.PushDelivery;
import com.optiflow.platform.notification.application.outboundservices.PushNotificationService;
import com.optiflow.platform.notification.domain.exceptions.NotificationNotFoundException;
import com.optiflow.platform.notification.domain.model.aggregates.Notification;
import com.optiflow.platform.notification.domain.model.commands.SendPushNotificationCommand;
import com.optiflow.platform.notification.domain.model.entities.DeviceToken;
import com.optiflow.platform.notification.domain.model.events.PushNotificationSent;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.model.valueobjects.PushStatus;
import com.optiflow.platform.notification.domain.repositories.DeviceTokenRepository;
import com.optiflow.platform.notification.domain.repositories.NotificationRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Sends a pending notification to the devices of the patient. A notification that is not due yet
 * is left for the scheduled dispatcher; one that cannot be delivered is marked as failed and
 * stays visible in the app.
 */
@Service
public class SendPushNotificationHandler {

  private final NotificationRepository notificationRepository;
  private final DeviceTokenRepository deviceTokenRepository;
  private final PushNotificationService pushNotificationService;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public SendPushNotificationHandler(
      NotificationRepository notificationRepository,
      DeviceTokenRepository deviceTokenRepository,
      PushNotificationService pushNotificationService,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.notificationRepository = notificationRepository;
    this.deviceTokenRepository = deviceTokenRepository;
    this.pushNotificationService = pushNotificationService;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Notification handle(SendPushNotificationCommand command) {
    Notification notification = notificationRepository
        .findById(NotificationId.of(command.notificationId()))
        .orElseThrow(NotificationNotFoundException::new);
    Instant now = clock.instant();
    if (notification.pushStatus() != PushStatus.PENDING || !notification.isDue(now)) {
      return notification;
    }
    List<DeviceToken> devices = deviceTokenRepository.findByPatientId(notification.patientId());
    PushDelivery delivery = devices.isEmpty()
        ? PushDelivery.NONE
        : pushNotificationService.send(notification, devices);
    delivery.invalidTokens().forEach(deviceTokenRepository::deleteByToken);
    if (delivery.delivered()) {
      notification.markPushSent();
      eventPublisher.publish(new PushNotificationSent(
          notification.id(), notification.patientId(), delivery.deliveredDevices(), now));
    } else {
      notification.markPushFailed();
    }
    notificationRepository.save(notification);
    return notification;
  }
}
