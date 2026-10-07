package com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.searchbooking.domain.entities.Patient;
import com.optiflow.platform.searchbooking.domain.repositories.PatientRepository;
import com.optiflow.platform.searchbooking.domain.valueobjects.EmailAddress;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.mappers.PatientMapper;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class PatientRepositoryImpl implements PatientRepository {

  private final PatientJpaRepository patientJpaRepository;
  private final PatientMapper patientMapper;

  public PatientRepositoryImpl(
      PatientJpaRepository patientJpaRepository, PatientMapper patientMapper) {
    this.patientJpaRepository = patientJpaRepository;
    this.patientMapper = patientMapper;
  }

  @Override
  public void save(Patient patient, String passwordHash) {
    patientJpaRepository.save(patientMapper.toEntity(patient, passwordHash));
  }

  @Override
  public Optional<Patient> findById(PatientId id) {
    return patientJpaRepository.findById(id.value()).map(patientMapper::toDomain);
  }

  @Override
  public Optional<Patient> findByEmail(EmailAddress email) {
    return patientJpaRepository.findByEmail(email.value()).map(patientMapper::toDomain);
  }

  @Override
  public Optional<String> findPasswordHashByEmail(EmailAddress email) {
    return patientJpaRepository.findByEmail(email.value()).map(entity -> entity.getPasswordHash());
  }
}
