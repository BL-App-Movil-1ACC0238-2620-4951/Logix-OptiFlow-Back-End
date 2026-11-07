package com.optiflow.platform.clinical.application.eventhandlers;

import com.optiflow.platform.clinical.application.commandhandlers.ExaminePatientHandler;
import com.optiflow.platform.clinical.domain.model.commands.ExaminePatientCommand;
import com.optiflow.platform.clinical.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.clinical.domain.model.valueobjects.PatientId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Reacts to a booked appointment received by the {@code AppointmentBookedConsumer} and opens the
 * clinical record by running the {@code ExaminePatient} command.
 *
 * <p>A failure is logged and never rolls back the appointment.
 */
@Component("clinicalAppointmentBookedEventHandler")
public class AppointmentBookedEventHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AppointmentBookedEventHandler.class);

  private final ExaminePatientHandler examinePatientHandler;

  public AppointmentBookedEventHandler(ExaminePatientHandler examinePatientHandler) {
    this.examinePatientHandler = examinePatientHandler;
  }

  public void handle(AppointmentId appointmentId, PatientId patientId) {
    try {
      examinePatientHandler.handle(new ExaminePatientCommand(appointmentId, patientId));
    } catch (RuntimeException exception) {
      LOGGER.error(
          "Could not open the clinical record for appointment {}.",
          appointmentId.value(),
          exception);
    }
  }
}
