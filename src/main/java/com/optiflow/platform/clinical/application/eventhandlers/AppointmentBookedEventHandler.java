package com.optiflow.platform.clinical.application.eventhandlers;

import com.optiflow.platform.clinical.application.commands.ExaminePatientCommand;
import com.optiflow.platform.clinical.application.services.ExaminePatientHandler;
import com.optiflow.platform.clinical.domain.valueobjects.AppointmentId;
import com.optiflow.platform.clinical.domain.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.domain.events.AppointmentBooked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Translates the Search & Booking {@link AppointmentBooked} event into the internal
 * {@code ExaminePatient} command (anti-corruption layer described in the context map).
 *
 * <p>It runs after the booking commits, so a failure here never rolls back the appointment.
 */
@Component("clinicalAppointmentBookedEventHandler")
public class AppointmentBookedEventHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AppointmentBookedEventHandler.class);

  private final ExaminePatientHandler examinePatientHandler;

  public AppointmentBookedEventHandler(ExaminePatientHandler examinePatientHandler) {
    this.examinePatientHandler = examinePatientHandler;
  }

  @TransactionalEventListener(fallbackExecution = true)
  public void onAppointmentBooked(AppointmentBooked event) {
    try {
      examinePatientHandler.handle(new ExaminePatientCommand(
          AppointmentId.of(event.appointmentId().value()),
          PatientId.of(event.patientId().value())));
    } catch (RuntimeException exception) {
      LOGGER.error(
          "Could not open the clinical record for appointment {}.",
          event.appointmentId().value(),
          exception);
    }
  }
}
