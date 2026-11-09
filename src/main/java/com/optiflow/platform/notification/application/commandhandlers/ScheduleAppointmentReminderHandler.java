package com.optiflow.platform.notification.application.commandhandlers;

import com.optiflow.platform.notification.application.outboundservices.ExternalPatientDirectoryService;
import com.optiflow.platform.notification.application.outboundservices.ScheduledAppointment;
import com.optiflow.platform.notification.domain.model.aggregates.Notification;
import com.optiflow.platform.notification.domain.model.commands.ScheduleAppointmentReminderCommand;
import com.optiflow.platform.notification.domain.model.events.AppointmentReminderScheduled;
import com.optiflow.platform.notification.domain.model.events.InAppNotificationSent;
import com.optiflow.platform.notification.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationMessage;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationType;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import com.optiflow.platform.notification.domain.repositories.NotificationRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Schedules the reminder of a booked appointment 24 hours before it (right away if it is sooner)
 * and the reminder of the annual check-up 12 months after it.
 */
@Service
public class ScheduleAppointmentReminderHandler {

  private static final ZoneId LIMA = ZoneId.of("America/Lima");
  private static final Duration REMINDER_ADVANCE = Duration.ofHours(24);
  private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM").withZone(LIMA);
  private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm").withZone(LIMA);

  private final NotificationRepository notificationRepository;
  private final ExternalPatientDirectoryService patientDirectoryService;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public ScheduleAppointmentReminderHandler(
      NotificationRepository notificationRepository,
      ExternalPatientDirectoryService patientDirectoryService,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.notificationRepository = notificationRepository;
    this.patientDirectoryService = patientDirectoryService;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Notification handle(ScheduleAppointmentReminderCommand command) {
    PatientId patientId = PatientId.of(command.patientId());
    AppointmentId appointmentId = AppointmentId.of(command.appointmentId());
    if (notificationRepository.existsByPatientIdAndAppointmentId(patientId, appointmentId)) {
      return notificationRepository.findByPatientId(patientId).stream()
          .filter(notification -> notification.type() == NotificationType.APPOINTMENT_REMINDER)
          .filter(notification -> notification.appointmentId().filter(appointmentId::equals)
              .isPresent())
          .findFirst()
          .orElseThrow();
    }
    ScheduledAppointment appointment = patientDirectoryService.findAppointment(appointmentId)
        .orElseThrow(() -> new DomainException("Appointment was not found.", 404));
    Instant now = clock.instant();
    Instant reminderAt = latest(now, appointment.startsAt().minus(REMINDER_ADVANCE));
    Instant annualCheckupAt = appointment.startsAt().atZone(LIMA).plusYears(1).toInstant();

    Notification reminder = Notification.scheduleAppointmentReminder(
        NotificationId.generate(),
        patientId,
        appointmentId,
        new NotificationMessage(
            "Recordatorio de cita",
            "Tu evaluación visual es el " + DATE.format(appointment.startsAt())
                + " a las " + TIME.format(appointment.startsAt()) + "."),
        reminderAt,
        now);
    Notification annualCheckup = Notification.scheduleAnnualCheckupReminder(
        NotificationId.generate(),
        patientId,
        appointmentId,
        new NotificationMessage(
            "Control visual anual",
            "Ya pasó un año desde tu última evaluación visual. "
                + "Reserva tu control en OptiFlow."),
        annualCheckupAt,
        now);
    notificationRepository.save(reminder);
    notificationRepository.save(annualCheckup);
    eventPublisher.publish(new AppointmentReminderScheduled(
        appointmentId, patientId, reminderAt, annualCheckupAt, now));
    if (reminder.isDue(now)) {
      eventPublisher.publish(new InAppNotificationSent(
          reminder.id(), patientId, reminder.type(), now));
    }
    return reminder;
  }

  private static Instant latest(Instant first, Instant second) {
    return first.isAfter(second) ? first : second;
  }
}
