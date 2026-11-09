package com.optiflow.platform.notification.application.commandhandlers;

import com.optiflow.platform.notification.domain.exceptions.NotificationNotFoundException;
import com.optiflow.platform.notification.domain.model.aggregates.Notification;
import com.optiflow.platform.notification.domain.model.commands.AnswerSatisfactionSurveyCommand;
import com.optiflow.platform.notification.domain.model.events.SatisfactionSurveyCompleted;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.repositories.NotificationRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerSatisfactionSurveyHandler {

  private final NotificationRepository notificationRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public AnswerSatisfactionSurveyHandler(
      NotificationRepository notificationRepository,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.notificationRepository = notificationRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public Notification handle(AnswerSatisfactionSurveyCommand command) {
    Notification survey = notificationRepository
        .findById(NotificationId.of(command.notificationId()))
        .orElseThrow(NotificationNotFoundException::new);
    Instant now = clock.instant();
    survey.answerSurvey(command.rating(), command.comment(), now);
    notificationRepository.save(survey);
    eventPublisher.publish(new SatisfactionSurveyCompleted(
        survey.id(), survey.patientId(), command.rating(), now));
    return survey;
  }
}
