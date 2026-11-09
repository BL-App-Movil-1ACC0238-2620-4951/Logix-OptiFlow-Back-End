package com.optiflow.platform.notification.application.eventhandlers;

import com.optiflow.platform.notification.application.commandhandlers.SendPushNotificationHandler;
import com.optiflow.platform.notification.application.outboundservices.ExternalPatientDirectoryService;
import com.optiflow.platform.notification.domain.model.aggregates.Notification;
import com.optiflow.platform.notification.domain.model.commands.SendPushNotificationCommand;
import com.optiflow.platform.notification.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.notification.domain.repositories.NotificationRepository;
import java.time.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Every 5 minutes sends the notifications whose scheduled date arrived. Before a reminder is sent
 * it checks the appointment in Search &amp; Booking; the reminders of a cancelled appointment are
 * deleted without being sent.
 */
@Component
public class ScheduledNotificationDispatcher {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ScheduledNotificationDispatcher.class);

  private final NotificationRepository notificationRepository;
  private final ExternalPatientDirectoryService patientDirectoryService;
  private final SendPushNotificationHandler sendPushNotificationHandler;
  private final Clock clock;

  public ScheduledNotificationDispatcher(
      NotificationRepository notificationRepository,
      ExternalPatientDirectoryService patientDirectoryService,
      SendPushNotificationHandler sendPushNotificationHandler,
      Clock clock) {
    this.notificationRepository = notificationRepository;
    this.patientDirectoryService = patientDirectoryService;
    this.sendPushNotificationHandler = sendPushNotificationHandler;
    this.clock = clock;
  }

  @Scheduled(initialDelayString = "PT1M", fixedDelayString = "PT5M")
  public void dispatch() {
    for (Notification notification
        : notificationRepository.findPendingDue(clock.instant())) {
      try {
        if (notification.isReminder() && appointmentWasCancelled(notification)) {
          notificationRepository.deleteByAppointmentId(notification.appointmentId().orElseThrow());
        } else {
          sendPushNotificationHandler.handle(
              new SendPushNotificationCommand(notification.id().value()));
        }
      } catch (RuntimeException exception) {
        LOGGER.error("Could not dispatch notification {}.", notification.id().value(), exception);
      }
    }
  }

  private boolean appointmentWasCancelled(Notification notification) {
    AppointmentId appointmentId = notification.appointmentId().orElseThrow();
    return patientDirectoryService.findAppointment(appointmentId)
        .map(appointment -> appointment.cancelled())
        .orElse(true);
  }
}
