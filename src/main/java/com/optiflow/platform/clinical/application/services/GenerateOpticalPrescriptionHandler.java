package com.optiflow.platform.clinical.application.services;

import com.optiflow.platform.clinical.application.commands.GenerateOpticalPrescriptionCommand;
import com.optiflow.platform.clinical.domain.entities.ClinicalRecord;
import com.optiflow.platform.clinical.domain.events.OpticalPrescriptionGenerated;
import com.optiflow.platform.clinical.domain.exceptions.ClinicalRecordNotFoundException;
import com.optiflow.platform.clinical.domain.repositories.ClinicalRecordRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GenerateOpticalPrescriptionHandler {

  private final ClinicalRecordRepository clinicalRecordRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public GenerateOpticalPrescriptionHandler(
      ClinicalRecordRepository clinicalRecordRepository,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.clinicalRecordRepository = clinicalRecordRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public ClinicalRecord handle(GenerateOpticalPrescriptionCommand command) {
    ClinicalRecord clinicalRecord = clinicalRecordRepository.findById(command.clinicalRecordId())
        .orElseThrow(ClinicalRecordNotFoundException::new);
    clinicalRecord.generatePrescription(command.prescription());
    clinicalRecordRepository.save(clinicalRecord);
    eventPublisher.publish(new OpticalPrescriptionGenerated(
        clinicalRecord.id(), clinicalRecord.patientId(), clock.instant()));
    return clinicalRecord;
  }
}
