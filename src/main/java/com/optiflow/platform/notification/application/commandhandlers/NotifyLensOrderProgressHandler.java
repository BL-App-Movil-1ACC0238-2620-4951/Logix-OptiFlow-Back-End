package com.optiflow.platform.notification.application.commandhandlers;

import com.optiflow.platform.notification.domain.model.aggregates.Notification;
import com.optiflow.platform.notification.domain.model.commands.NotifyLensOrderProgressCommand;
import com.optiflow.platform.notification.domain.model.events.InAppNotificationSent;
import com.optiflow.platform.notification.domain.model.events.LensOrderProgressNotified;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationMessage;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import com.optiflow.platform.notification.domain.model.valueobjects.WorkOrderId;
import com.optiflow.platform.notification.domain.repositories.NotificationRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotifyLensOrderProgressHandler {

  private final NotificationRepository notificationRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public NotifyLensOrderProgressHandler(
      NotificationRepository notificationRepository,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.notificationRepository = notificationRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Notification handle(NotifyLensOrderProgressCommand command) {
    PatientId patientId = PatientId.of(command.patientId());
    WorkOrderId workOrderId = WorkOrderId.of(command.workOrderId());
    Instant now = clock.instant();
    Notification notification = Notification.notifyLensOrderProgress(
        NotificationId.generate(),
        patientId,
        workOrderId,
        new NotificationMessage("Lens order update", command.progressSummary()),
        now);
    notificationRepository.save(notification);
    eventPublisher.publish(new LensOrderProgressNotified(
        notification.id(), patientId, workOrderId, command.progressSummary(), now));
    eventPublisher.publish(new InAppNotificationSent(
        notification.id(), patientId, notification.type(), now));
    return notification;
  }
}
