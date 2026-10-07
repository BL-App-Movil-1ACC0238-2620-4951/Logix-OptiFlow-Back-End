package com.optiflow.platform.notification.application.services;

import com.optiflow.platform.notification.application.commands.ScheduleAppointmentReminderCommand;
import com.optiflow.platform.notification.domain.entities.Notification;
import com.optiflow.platform.notification.domain.events.InAppNotificationSent;
import com.optiflow.platform.notification.domain.repositories.NotificationRepository;
import com.optiflow.platform.notification.domain.valueobjects.AppointmentId;
import com.optiflow.platform.notification.domain.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.valueobjects.NotificationMessage;
import com.optiflow.platform.notification.domain.valueobjects.PatientId;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScheduleAppointmentReminderHandler {

  private final NotificationRepository notificationRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public ScheduleAppointmentReminderHandler(
      NotificationRepository notificationRepository,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.notificationRepository = notificationRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public Notification handle(ScheduleAppointmentReminderCommand command) {
    PatientId patientId = PatientId.of(command.patientId());
    AppointmentId appointmentId = AppointmentId.of(command.appointmentId());
    if (notificationRepository.existsByPatientIdAndAppointmentId(patientId, appointmentId)) {
      return notificationRepository.findByPatientId(patientId).stream()
          .filter(notification -> notification.appointmentId().filter(appointmentId::equals).isPresent())
          .findFirst()
          .orElseThrow();
    }
    Instant now = clock.instant();
    Notification notification = Notification.scheduleAppointmentReminder(
        NotificationId.generate(),
        patientId,
        appointmentId,
        new NotificationMessage(
            "Appointment confirmed",
            "Your optometry appointment was booked successfully. We will remind you before the visit."),
        now);
    notificationRepository.save(notification);
    eventPublisher.publish(new InAppNotificationSent(
        notification.id(), patientId, notification.type(), now));
    return notification;
  }
}
