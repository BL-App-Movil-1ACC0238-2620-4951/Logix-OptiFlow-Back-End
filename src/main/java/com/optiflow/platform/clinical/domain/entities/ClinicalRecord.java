package com.optiflow.platform.clinical.domain.entities;

import com.optiflow.platform.clinical.domain.valueobjects.AppointmentId;
import com.optiflow.platform.clinical.domain.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.valueobjects.ClinicalRecordStatus;
import com.optiflow.platform.clinical.domain.valueobjects.OpticalPrescription;
import com.optiflow.platform.clinical.domain.valueobjects.PatientId;
import com.optiflow.platform.shared.exceptions.DomainException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class ClinicalRecord {

  private static final int MAX_OBSERVATIONS_LENGTH = 500;

  private final ClinicalRecordId id;
  private final PatientId patientId;
  private final AppointmentId appointmentId;
  private Instant examinationDate;
  private String observations;
  private ClinicalRecordStatus status;
  private MedicalHistory medicalHistory;
  private OpticalPrescription prescription;

  private ClinicalRecord(
      ClinicalRecordId id,
      PatientId patientId,
      AppointmentId appointmentId,
      Instant examinationDate,
      String observations,
      ClinicalRecordStatus status,
      MedicalHistory medicalHistory,
      OpticalPrescription prescription) {
    this.id = id;
    this.patientId = patientId;
    this.appointmentId = appointmentId;
    this.examinationDate = examinationDate;
    this.observations = observations;
    this.status = status;
    this.medicalHistory = medicalHistory;
    this.prescription = prescription;
  }

  /** Opens a pending record for a booked appointment, before the patient is examined. */
  public static ClinicalRecord open(PatientId patientId, AppointmentId appointmentId) {
    return new ClinicalRecord(
        ClinicalRecordId.generate(), patientId, appointmentId, null, null,
        ClinicalRecordStatus.PENDING, null, null);
  }

  /** Registers a record whose examination already took place. */
  public static ClinicalRecord register(
      PatientId patientId,
      AppointmentId appointmentId,
      Instant examinationDate,
      String observations) {
    ClinicalRecord record = open(patientId, appointmentId);
    record.registerExamination(examinationDate, observations);
    return record;
  }

  public static ClinicalRecord reconstitute(
      ClinicalRecordId id,
      PatientId patientId,
      AppointmentId appointmentId,
      Instant examinationDate,
      String observations,
      ClinicalRecordStatus status,
      MedicalHistory medicalHistory,
      OpticalPrescription prescription) {
    return new ClinicalRecord(
        id, patientId, appointmentId, examinationDate, observations, status, medicalHistory,
        prescription);
  }

  public void registerExamination(Instant examinationDate, String observations) {
    if (status != ClinicalRecordStatus.PENDING) {
      throw new DomainException(
          "The clinical record for this appointment is already registered.", 409);
    }
    if (examinationDate == null) {
      throw new DomainException("Examination date is required.", 400);
    }
    this.examinationDate = examinationDate;
    this.observations = normalizeObservations(observations);
    this.status = ClinicalRecordStatus.OPEN;
  }

  public void recordMedicalHistory(
      List<String> allergies,
      List<String> previousConditions,
      String familyOcularHistory,
      Instant now) {
    if (status == ClinicalRecordStatus.CLOSED) {
      throw new DomainException("A closed clinical record cannot be modified.", 409);
    }
    if (medicalHistory == null) {
      medicalHistory = MedicalHistory.record(
          allergies, previousConditions, familyOcularHistory, now);
    } else {
      medicalHistory.update(allergies, previousConditions, familyOcularHistory, now);
    }
  }

  public void generatePrescription(OpticalPrescription prescription) {
    if (status != ClinicalRecordStatus.OPEN) {
      throw new DomainException(
          "The patient must be examined before generating a prescription.", 409);
    }
    if (prescription == null) {
      throw new DomainException("Optical prescription is required.", 400);
    }
    this.prescription = prescription;
  }

  public void close() {
    if (status != ClinicalRecordStatus.OPEN) {
      throw new DomainException("Only an open clinical record can be closed.", 409);
    }
    status = ClinicalRecordStatus.CLOSED;
  }

  public boolean hasPrescription() {
    return prescription != null;
  }

  private static String normalizeObservations(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    String trimmed = value.trim();
    if (trimmed.length() > MAX_OBSERVATIONS_LENGTH) {
      throw new DomainException("Observations must have at most 500 characters.", 400);
    }
    return trimmed;
  }

  public ClinicalRecordId id() {
    return id;
  }

  public PatientId patientId() {
    return patientId;
  }

  public AppointmentId appointmentId() {
    return appointmentId;
  }

  public Instant examinationDate() {
    return examinationDate;
  }

  public String observations() {
    return observations;
  }

  public ClinicalRecordStatus status() {
    return status;
  }

  public Optional<MedicalHistory> medicalHistory() {
    return Optional.ofNullable(medicalHistory);
  }

  public Optional<OpticalPrescription> prescription() {
    return Optional.ofNullable(prescription);
  }
}
