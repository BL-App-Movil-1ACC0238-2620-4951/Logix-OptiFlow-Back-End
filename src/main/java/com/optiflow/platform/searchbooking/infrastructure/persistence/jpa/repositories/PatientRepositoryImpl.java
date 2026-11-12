package com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.searchbooking.domain.model.entities.Patient;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.EmailAddress;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.domain.repositories.PatientRepository;
import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities.PatientEntity;
import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.mappers.PatientMapper;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
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
  public void update(Patient patient) {
    PatientEntity entity = patientJpaRepository.findById(patient.id().value())
        .orElseThrow(() -> new DomainException("The patient was not found.", 404));
    entity.setFullName(patient.name().value());
    entity.setPhone(patient.phone().value());
    entity.setBirthDate(patient.birthDate().orElse(null));
    patientJpaRepository.save(entity);
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
  public List<Patient> search(String name, String email) {
    return patientJpaRepository.search(normalize(name), normalize(email)).stream()
        .map(patientMapper::toDomain)
        .toList();
  }

  @Override
  public Optional<String> findPasswordHashByEmail(EmailAddress email) {
    return patientJpaRepository.findByEmail(email.value()).map(entity -> entity.getPasswordHash());
  }

  @Override
  public List<PatientId> findIdsByBirthday(LocalDate date) {
    return patientJpaRepository.findIdsByBirthMonthAndDay(date.getMonthValue(), date.getDayOfMonth())
        .stream()
        .map(PatientId::of)
        .toList();
  }

  private static String normalize(String text) {
    return text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
  }
}
