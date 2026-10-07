package com.optiflow.booking.infrastructure.persistence;

import com.optiflow.booking.domain.model.Patient;
import com.optiflow.booking.domain.repository.PatientRepository;
import com.optiflow.booking.domain.vo.EmailAddress;
import com.optiflow.booking.domain.vo.PatientId;
import com.optiflow.booking.infrastructure.persistence.jpa.PatientJpaRepository;
import com.optiflow.booking.infrastructure.persistence.mapper.PatientMapper;
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
