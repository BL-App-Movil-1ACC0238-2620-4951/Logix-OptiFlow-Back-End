package com.optiflow.booking.infrastructure.persistence.mapper;

import com.optiflow.booking.domain.model.Patient;
import com.optiflow.booking.domain.vo.EmailAddress;
import com.optiflow.booking.domain.vo.Name;
import com.optiflow.booking.domain.vo.PatientId;
import com.optiflow.booking.domain.vo.PhoneNumber;
import com.optiflow.booking.infrastructure.persistence.entity.PatientEntity;
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
