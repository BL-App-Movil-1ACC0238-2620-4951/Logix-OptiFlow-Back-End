package com.optiflow.platform.clinical.application.services;

import com.optiflow.platform.clinical.application.commands.ExaminePatientCommand;
import com.optiflow.platform.clinical.domain.entities.ClinicalRecord;
import com.optiflow.platform.clinical.domain.events.ClinicalRecordRegistered;
import com.optiflow.platform.clinical.domain.repositories.ClinicalRecordRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** Opens the pending clinical record of a booked appointment. */
@Service
public class ExaminePatientHandler {

  private final ClinicalRecordRepository clinicalRecordRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public ExaminePatientHandler(
      ClinicalRecordRepository clinicalRecordRepository,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.clinicalRecordRepository = clinicalRecordRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public ClinicalRecord handle(ExaminePatientCommand command) {
    return clinicalRecordRepository.findByAppointmentId(command.appointmentId())
        .orElseGet(() -> {
          ClinicalRecord clinicalRecord =
              ClinicalRecord.open(command.patientId(), command.appointmentId());
          clinicalRecordRepository.save(clinicalRecord);
          eventPublisher.publish(new ClinicalRecordRegistered(
              clinicalRecord.id(),
              clinicalRecord.patientId(),
              clinicalRecord.appointmentId(),
              clock.instant()));
          return clinicalRecord;
        });
  }
}
