package com.optiflow.platform.notification.application.services;

import com.optiflow.platform.notification.application.commands.SendDeliveryDelayNotificationCommand;
import com.optiflow.platform.notification.domain.entities.Notification;
import com.optiflow.platform.notification.domain.events.InAppNotificationSent;
import com.optiflow.platform.notification.domain.repositories.NotificationRepository;
import com.optiflow.platform.notification.domain.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.valueobjects.NotificationMessage;
import com.optiflow.platform.notification.domain.valueobjects.PatientId;
import com.optiflow.platform.notification.domain.valueobjects.WorkOrderId;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SendDeliveryDelayNotificationHandler {

  private final NotificationRepository notificationRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public SendDeliveryDelayNotificationHandler(
      NotificationRepository notificationRepository,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.notificationRepository = notificationRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public Notification handle(SendDeliveryDelayNotificationCommand command) {
    PatientId patientId = PatientId.of(command.patientId());
    WorkOrderId workOrderId = WorkOrderId.of(command.workOrderId());
    Instant now = clock.instant();
    Notification notification = Notification.notifyDeliveryDelay(
        NotificationId.generate(),
        patientId,
        workOrderId,
        new NotificationMessage(
            "Delivery delay",
            "Your lens order delivery was delayed: " + command.reason()),
        now);
    notificationRepository.save(notification);
    eventPublisher.publish(new InAppNotificationSent(
        notification.id(), patientId, notification.type(), now));
    return notification;
  }
}
