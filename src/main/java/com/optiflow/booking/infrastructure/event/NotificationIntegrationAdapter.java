package com.optiflow.booking.infrastructure.event;

import com.optiflow.booking.domain.event.AppointmentBooked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificationIntegrationAdapter {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(NotificationIntegrationAdapter.class);

  public void requestAppointmentReminder(AppointmentBooked event) {
    LOGGER.info(
        "Reminder requested for patient {} and appointment {}.",
        event.patientId().value(),
        event.appointmentId().value());
  }
}
