package com.optiflow.platform.notification.application.commandhandlers;

import com.optiflow.platform.notification.domain.model.commands.DismissAppointmentReminderCommand;
import com.optiflow.platform.notification.domain.model.aggregates.Notification;
import com.optiflow.platform.notification.domain.repositories.NotificationRepository;
import com.optiflow.platform.notification.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import java.time.Clock;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DismissAppointmentReminderHandler {

  private final NotificationRepository notificationRepository;
  private final Clock clock;

  public DismissAppointmentReminderHandler(
      NotificationRepository notificationRepository, Clock clock) {
    this.notificationRepository = notificationRepository;
    this.clock = clock;
  }

  @Transactional
  public Optional<Notification> handle(DismissAppointmentReminderCommand command) {
    PatientId patientId = PatientId.of(command.patientId());
    AppointmentId appointmentId = AppointmentId.of(command.appointmentId());
    Optional<Notification> reminder = notificationRepository.findByPatientId(patientId).stream()
        .filter(notification -> notification.appointmentId().filter(appointmentId::equals).isPresent())
        .findFirst();
    reminder.ifPresent(notification -> {
      notification.markAsRead(clock.instant());
      notificationRepository.save(notification);
    });
    return reminder;
  }
}
