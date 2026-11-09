package com.optiflow.platform.notification.application.commandhandlers;

import com.optiflow.platform.notification.domain.model.aggregates.Notification;
import com.optiflow.platform.notification.domain.model.commands.SendSatisfactionSurveyCommand;
import com.optiflow.platform.notification.domain.model.events.InAppNotificationSent;
import com.optiflow.platform.notification.domain.model.events.SatisfactionSurveyWasSent;
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
public class SendSatisfactionSurveyHandler {

  private final NotificationRepository notificationRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public SendSatisfactionSurveyHandler(
      NotificationRepository notificationRepository,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.notificationRepository = notificationRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Notification handle(SendSatisfactionSurveyCommand command) {
    PatientId patientId = PatientId.of(command.patientId());
    WorkOrderId workOrderId = WorkOrderId.of(command.workOrderId());
    Instant now = clock.instant();
    Notification survey = Notification.sendSatisfactionSurvey(
        NotificationId.generate(),
        patientId,
        workOrderId,
        new NotificationMessage(
            "Encuesta de satisfacción",
            "¿Cómo fue tu experiencia con tu pedido? Califícanos del 1 al 5."),
        now);
    notificationRepository.save(survey);
    eventPublisher.publish(new SatisfactionSurveyWasSent(survey.id(), patientId, workOrderId, now));
    eventPublisher.publish(new InAppNotificationSent(survey.id(), patientId, survey.type(), now));
    return survey;
  }
}
