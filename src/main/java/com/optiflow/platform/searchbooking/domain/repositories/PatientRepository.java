package com.optiflow.platform.searchbooking.domain.repositories;

import com.optiflow.platform.searchbooking.domain.entities.Patient;
import com.optiflow.platform.searchbooking.domain.valueobjects.EmailAddress;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import java.util.Optional;

public interface PatientRepository {

  void save(Patient patient, String passwordHash);

  Optional<Patient> findById(PatientId id);

  Optional<Patient> findByEmail(EmailAddress email);

  Optional<String> findPasswordHashByEmail(EmailAddress email);
}
