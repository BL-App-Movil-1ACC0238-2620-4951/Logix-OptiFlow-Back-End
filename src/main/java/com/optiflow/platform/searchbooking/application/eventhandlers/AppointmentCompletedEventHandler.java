package com.optiflow.platform.searchbooking.application.eventhandlers;

import com.optiflow.platform.searchbooking.domain.model.events.AppointmentCompleted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AppointmentCompletedEventHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AppointmentCompletedEventHandler.class);

  @EventListener
  public void onAppointmentCompleted(AppointmentCompleted event) {
    LOGGER.info(
        "Appointment {} was completed for patient {}.",
        event.appointmentId().value(),
        event.patientId().value());
  }
}
