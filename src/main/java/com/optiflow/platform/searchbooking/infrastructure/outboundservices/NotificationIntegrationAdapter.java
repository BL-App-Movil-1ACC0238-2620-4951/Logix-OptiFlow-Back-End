package com.optiflow.platform.searchbooking.infrastructure.outboundservices;

import com.optiflow.platform.searchbooking.domain.model.events.AppointmentBooked;
import com.optiflow.platform.searchbooking.domain.model.events.AppointmentCancelled;
import com.optiflow.platform.searchbooking.domain.model.events.AppointmentCompleted;
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

  public void dismissAppointmentReminder(AppointmentCancelled event) {
    LOGGER.info(
        "Reminder dismissal requested for patient {} and appointment {}.",
        event.patientId().value(),
        event.appointmentId().value());
  }

  public void recordAppointmentCompleted(AppointmentCompleted event) {
    LOGGER.info(
        "Appointment completion recorded for patient {} and appointment {}.",
        event.patientId().value(),
        event.appointmentId().value());
  }
}
