package com.optiflow.platform.clinical.application.services;

import com.optiflow.platform.clinical.application.commands.RecordMedicalHistoryCommand;
import com.optiflow.platform.clinical.domain.entities.ClinicalRecord;
import com.optiflow.platform.clinical.domain.events.MedicalHistoryRecorded;
import com.optiflow.platform.clinical.domain.exceptions.ClinicalRecordNotFoundException;
import com.optiflow.platform.clinical.domain.repositories.ClinicalRecordRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecordMedicalHistoryHandler {

  private final ClinicalRecordRepository clinicalRecordRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public RecordMedicalHistoryHandler(
      ClinicalRecordRepository clinicalRecordRepository,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.clinicalRecordRepository = clinicalRecordRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public ClinicalRecord handle(RecordMedicalHistoryCommand command) {
    ClinicalRecord clinicalRecord = clinicalRecordRepository.findById(command.clinicalRecordId())
        .orElseThrow(ClinicalRecordNotFoundException::new);
    Instant now = clock.instant();
    clinicalRecord.recordMedicalHistory(
        command.allergies(), command.previousConditions(), command.familyOcularHistory(), now);
    clinicalRecordRepository.save(clinicalRecord);
    eventPublisher.publish(
        new MedicalHistoryRecorded(clinicalRecord.id(), clinicalRecord.patientId(), now));
    return clinicalRecord;
  }
}
