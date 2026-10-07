package com.optiflow.platform.clinical.application.services;

import com.optiflow.platform.clinical.application.queries.GetClinicalRecordByIdQuery;
import com.optiflow.platform.clinical.application.queries.GetClinicalRecordsByPatientIdQuery;
import com.optiflow.platform.clinical.application.queries.GetOpticalPrescriptionQuery;
import com.optiflow.platform.clinical.domain.entities.ClinicalRecord;
import com.optiflow.platform.clinical.domain.exceptions.ClinicalRecordNotFoundException;
import com.optiflow.platform.clinical.domain.repositories.ClinicalRecordRepository;
import com.optiflow.platform.shared.exceptions.DomainException;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClinicalRecordQueryService {

  /** Pending records (no examination date yet) first, then the most recent examinations. */
  private static final Comparator<ClinicalRecord> MOST_RECENT_FIRST = Comparator.comparing(
      ClinicalRecord::examinationDate, Comparator.nullsFirst(Comparator.<Instant>reverseOrder()));

  private final ClinicalRecordRepository clinicalRecordRepository;

  public ClinicalRecordQueryService(ClinicalRecordRepository clinicalRecordRepository) {
    this.clinicalRecordRepository = clinicalRecordRepository;
  }

  @Transactional(readOnly = true)
  public ClinicalRecord handle(GetClinicalRecordByIdQuery query) {
    return clinicalRecordRepository.findById(query.clinicalRecordId())
        .orElseThrow(ClinicalRecordNotFoundException::new);
  }

  @Transactional(readOnly = true)
  public List<ClinicalRecord> handle(GetClinicalRecordsByPatientIdQuery query) {
    return clinicalRecordRepository.findByPatientId(query.patientId()).stream()
        .sorted(MOST_RECENT_FIRST)
        .toList();
  }

  @Transactional(readOnly = true)
  public ClinicalRecord handle(GetOpticalPrescriptionQuery query) {
    ClinicalRecord clinicalRecord = clinicalRecordRepository.findById(query.clinicalRecordId())
        .orElseThrow(ClinicalRecordNotFoundException::new);
    if (!clinicalRecord.hasPrescription()) {
      throw new DomainException("The clinical record has no optical prescription yet.", 404);
    }
    return clinicalRecord;
  }
}
