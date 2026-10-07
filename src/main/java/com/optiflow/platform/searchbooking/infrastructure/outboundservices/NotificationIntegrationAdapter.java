package com.optiflow.platform.searchbooking.infrastructure.outboundservices;

import com.optiflow.platform.searchbooking.domain.events.AppointmentBooked;
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
