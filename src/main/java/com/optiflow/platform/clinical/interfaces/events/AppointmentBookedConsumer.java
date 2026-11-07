package com.optiflow.platform.clinical.interfaces.events;

import com.optiflow.platform.clinical.application.eventhandlers.AppointmentBookedEventHandler;
import com.optiflow.platform.clinical.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.clinical.domain.model.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.domain.model.events.AppointmentBooked;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Subscribes to the Search & Booking {@link AppointmentBooked} event and translates it into the
 * identifiers of this context (anti-corruption layer described in the context map).
 *
 * <p>It runs after the booking commits.
 */
@Component
public class AppointmentBookedConsumer {

  private final AppointmentBookedEventHandler appointmentBookedEventHandler;

  public AppointmentBookedConsumer(AppointmentBookedEventHandler appointmentBookedEventHandler) {
    this.appointmentBookedEventHandler = appointmentBookedEventHandler;
  }

  @TransactionalEventListener(fallbackExecution = true)
  public void onAppointmentBooked(AppointmentBooked event) {
    appointmentBookedEventHandler.handle(
        AppointmentId.of(event.appointmentId().value()),
        PatientId.of(event.patientId().value()));
  }
}
