package com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.mappers;

import com.optiflow.platform.searchbooking.domain.entities.Patient;
import com.optiflow.platform.searchbooking.domain.valueobjects.EmailAddress;
import com.optiflow.platform.searchbooking.domain.valueobjects.Name;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.domain.valueobjects.PhoneNumber;
import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities.PatientEntity;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

  public Patient toDomain(PatientEntity entity) {
    return Patient.reconstitute(
        PatientId.of(entity.getId()),
        new Name(entity.getFullName()),
        new EmailAddress(entity.getEmail()),
        new PhoneNumber(entity.getPhone()),
        entity.getCreatedAt());
  }

  public PatientEntity toEntity(Patient patient, String passwordHash) {
    PatientEntity entity = new PatientEntity();
    entity.setId(patient.id().value());
    entity.setFullName(patient.name().value());
    entity.setEmail(patient.email().value());
    entity.setPhone(patient.phone().value());
    entity.setPasswordHash(passwordHash);
    entity.setCreatedAt(patient.createdAt());
    return entity;
  }
}
