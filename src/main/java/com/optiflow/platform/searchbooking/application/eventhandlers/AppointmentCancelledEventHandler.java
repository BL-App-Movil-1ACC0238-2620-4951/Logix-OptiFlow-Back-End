package com.optiflow.platform.searchbooking.application.eventhandlers;

import com.optiflow.platform.searchbooking.domain.events.AppointmentCancelled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AppointmentCancelledEventHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AppointmentCancelledEventHandler.class);

  @EventListener
  public void onAppointmentCancelled(AppointmentCancelled event) {
    LOGGER.info(
        "Appointment {} was cancelled. The time slot {} is available again.",
        event.appointmentId().value(),
        event.timeSlotId().value());
  }
}
