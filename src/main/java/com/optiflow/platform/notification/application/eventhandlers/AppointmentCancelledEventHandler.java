package com.optiflow.platform.notification.application.eventhandlers;

import com.optiflow.platform.notification.application.commandhandlers.DismissAppointmentReminderHandler;
import com.optiflow.platform.notification.domain.model.commands.DismissAppointmentReminderCommand;
import com.optiflow.platform.searchbooking.domain.model.events.AppointmentCancelled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component("notificationAppointmentCancelledEventHandler")
public class AppointmentCancelledEventHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AppointmentCancelledEventHandler.class);

  private final DismissAppointmentReminderHandler dismissAppointmentReminderHandler;

  public AppointmentCancelledEventHandler(
      DismissAppointmentReminderHandler dismissAppointmentReminderHandler) {
    this.dismissAppointmentReminderHandler = dismissAppointmentReminderHandler;
  }

  @TransactionalEventListener(fallbackExecution = true)
  public void onAppointmentCancelled(AppointmentCancelled event) {
    try {
      dismissAppointmentReminderHandler.handle(new DismissAppointmentReminderCommand(
          event.patientId().value(), event.appointmentId().value()));
    } catch (RuntimeException exception) {
      LOGGER.error(
          "Could not dismiss the appointment reminder for appointment {}.",
          event.appointmentId().value(),
          exception);
    }
  }
}
