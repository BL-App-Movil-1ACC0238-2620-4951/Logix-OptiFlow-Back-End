package com.optiflow.platform.clinical.application.services;

import com.optiflow.platform.clinical.application.commands.RegisterClinicalRecordCommand;
import com.optiflow.platform.clinical.domain.entities.ClinicalRecord;
import com.optiflow.platform.clinical.domain.events.ClinicalRecordRegistered;
import com.optiflow.platform.clinical.domain.events.PatientExamined;
import com.optiflow.platform.clinical.domain.repositories.ClinicalRecordRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import com.optiflow.platform.shared.exceptions.DomainException;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Registers the examination of a patient, completing the record opened at booking time. */
@Service
public class RegisterClinicalRecordHandler {

  private final ClinicalRecordRepository clinicalRecordRepository;
  private final ExternalAppointmentService externalAppointmentService;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public RegisterClinicalRecordHandler(
      ClinicalRecordRepository clinicalRecordRepository,
      ExternalAppointmentService externalAppointmentService,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.clinicalRecordRepository = clinicalRecordRepository;
    this.externalAppointmentService = externalAppointmentService;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public ClinicalRecord handle(RegisterClinicalRecordCommand command) {
    BookedAppointment appointment = externalAppointmentService.findById(command.appointmentId())
        .orElseThrow(() -> new DomainException("Appointment was not found.", 404));
    if (!appointment.patientId().equals(command.patientId())) {
      throw new DomainException("The appointment does not belong to this patient.", 409);
    }
    if (!appointment.active()) {
      throw new DomainException("A cancelled appointment cannot be registered.", 409);
    }
    Instant now = clock.instant();
    Instant examinationDate =
        command.examinationDate() == null ? now : command.examinationDate();

    Optional<ClinicalRecord> pending =
        clinicalRecordRepository.findByAppointmentId(command.appointmentId());
    ClinicalRecord clinicalRecord;
    if (pending.isPresent()) {
      clinicalRecord = pending.get();
      clinicalRecord.registerExamination(examinationDate, command.observations());
    } else {
      clinicalRecord = ClinicalRecord.register(
          command.patientId(), command.appointmentId(), examinationDate, command.observations());
    }
    clinicalRecordRepository.save(clinicalRecord);

    if (pending.isEmpty()) {
      eventPublisher.publish(new ClinicalRecordRegistered(
          clinicalRecord.id(), clinicalRecord.patientId(), clinicalRecord.appointmentId(), now));
    }
    eventPublisher.publish(new PatientExamined(
        clinicalRecord.id(), clinicalRecord.patientId(), clinicalRecord.appointmentId(), now));
    return clinicalRecord;
  }
}
