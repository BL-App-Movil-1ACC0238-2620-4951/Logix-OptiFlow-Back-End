package com.optiflow.platform.notification.application.eventhandlers;

import com.optiflow.platform.notification.application.commands.ScheduleAppointmentReminderCommand;
import com.optiflow.platform.notification.application.services.ScheduleAppointmentReminderHandler;
import com.optiflow.platform.searchbooking.domain.events.AppointmentBooked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Translates Search & Booking {@link AppointmentBooked} into an in-app appointment reminder.
 */
@Component("notificationAppointmentBookedEventHandler")
public class AppointmentBookedEventHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AppointmentBookedEventHandler.class);

  private final ScheduleAppointmentReminderHandler scheduleAppointmentReminderHandler;

  public AppointmentBookedEventHandler(
      ScheduleAppointmentReminderHandler scheduleAppointmentReminderHandler) {
    this.scheduleAppointmentReminderHandler = scheduleAppointmentReminderHandler;
  }

  @TransactionalEventListener(fallbackExecution = true)
  public void onAppointmentBooked(AppointmentBooked event) {
    try {
      scheduleAppointmentReminderHandler.handle(new ScheduleAppointmentReminderCommand(
          event.patientId().value(), event.appointmentId().value()));
    } catch (RuntimeException exception) {
      LOGGER.error(
          "Could not schedule the appointment reminder for appointment {}.",
          event.appointmentId().value(),
          exception);
    }
  }
}
