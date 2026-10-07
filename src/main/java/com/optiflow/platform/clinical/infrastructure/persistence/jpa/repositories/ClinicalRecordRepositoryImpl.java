package com.optiflow.platform.clinical.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.clinical.domain.entities.ClinicalRecord;
import com.optiflow.platform.clinical.domain.repositories.ClinicalRecordRepository;
import com.optiflow.platform.clinical.domain.valueobjects.AppointmentId;
import com.optiflow.platform.clinical.domain.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.valueobjects.PatientId;
import com.optiflow.platform.clinical.infrastructure.persistence.jpa.entities.ClinicalRecordEntity;
import com.optiflow.platform.clinical.infrastructure.persistence.jpa.mappers.ClinicalRecordMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ClinicalRecordRepositoryImpl implements ClinicalRecordRepository {

  private final ClinicalRecordJpaRepository clinicalRecordJpaRepository;
  private final MedicalHistoryJpaRepository medicalHistoryJpaRepository;
  private final OpticalPrescriptionJpaRepository opticalPrescriptionJpaRepository;
  private final ClinicalRecordMapper clinicalRecordMapper;

  public ClinicalRecordRepositoryImpl(
      ClinicalRecordJpaRepository clinicalRecordJpaRepository,
      MedicalHistoryJpaRepository medicalHistoryJpaRepository,
      OpticalPrescriptionJpaRepository opticalPrescriptionJpaRepository,
      ClinicalRecordMapper clinicalRecordMapper) {
    this.clinicalRecordJpaRepository = clinicalRecordJpaRepository;
    this.medicalHistoryJpaRepository = medicalHistoryJpaRepository;
    this.opticalPrescriptionJpaRepository = opticalPrescriptionJpaRepository;
    this.clinicalRecordMapper = clinicalRecordMapper;
  }

  @Override
  public void save(ClinicalRecord clinicalRecord) {
    clinicalRecordJpaRepository.save(clinicalRecordMapper.toEntity(clinicalRecord));
    clinicalRecord.medicalHistory().ifPresent(history -> medicalHistoryJpaRepository.save(
        clinicalRecordMapper.toMedicalHistoryEntity(clinicalRecord.id(), history)));
    clinicalRecord.prescription().ifPresent(prescription -> opticalPrescriptionJpaRepository.save(
        clinicalRecordMapper.toPrescriptionEntity(clinicalRecord.id(), prescription)));
  }

  @Override
  public Optional<ClinicalRecord> findById(ClinicalRecordId id) {
    return clinicalRecordJpaRepository.findById(id.value()).map(this::toDomain);
  }

  @Override
  public Optional<ClinicalRecord> findByAppointmentId(AppointmentId appointmentId) {
    return clinicalRecordJpaRepository.findByAppointmentId(appointmentId.value())
        .map(this::toDomain);
  }

  @Override
  public List<ClinicalRecord> findByPatientId(PatientId patientId) {
    return clinicalRecordJpaRepository.findByPatientId(patientId.value()).stream()
        .map(this::toDomain)
        .toList();
  }

  private ClinicalRecord toDomain(ClinicalRecordEntity entity) {
    return clinicalRecordMapper.toDomain(
        entity,
        medicalHistoryJpaRepository.findById(entity.getId()).orElse(null),
        opticalPrescriptionJpaRepository.findById(entity.getId()).orElse(null));
  }
}
