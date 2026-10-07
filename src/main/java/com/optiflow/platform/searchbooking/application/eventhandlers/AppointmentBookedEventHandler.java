package com.optiflow.platform.searchbooking.application.eventhandlers;

import com.optiflow.platform.searchbooking.domain.events.AppointmentBooked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AppointmentBookedEventHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentBookedEventHandler.class);

  @EventListener
  public void onAppointmentBooked(AppointmentBooked event) {
    LOGGER.info(
        "Appointment {} booked. Clinical & Commercial can start ExaminePatient.",
        event.appointmentId().value());
  }
}
