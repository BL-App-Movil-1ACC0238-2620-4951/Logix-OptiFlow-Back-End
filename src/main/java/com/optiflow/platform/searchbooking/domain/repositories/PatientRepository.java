package com.optiflow.platform.searchbooking.domain.repositories;

import com.optiflow.platform.searchbooking.domain.model.entities.Patient;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.EmailAddress;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PatientId;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PatientRepository {

  void save(Patient patient, String passwordHash);

  Optional<Patient> findById(PatientId id);

  Optional<Patient> findByEmail(EmailAddress email);

  /** Patients whose name and email contain the given texts, ignoring case; null matches all. */
  List<Patient> search(String name, String email);

  Optional<String> findPasswordHashByEmail(EmailAddress email);

  List<PatientId> findIdsByBirthday(LocalDate date);
}
