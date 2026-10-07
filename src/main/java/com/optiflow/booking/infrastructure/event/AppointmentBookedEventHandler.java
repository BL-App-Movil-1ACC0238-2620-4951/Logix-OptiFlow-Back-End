package com.optiflow.booking.infrastructure.event;

import com.optiflow.booking.domain.event.AppointmentBooked;
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
