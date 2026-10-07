package com.optiflow.booking.domain.repository;

import com.optiflow.booking.domain.model.Patient;
import com.optiflow.booking.domain.vo.EmailAddress;
import com.optiflow.booking.domain.vo.PatientId;
import java.util.Optional;

public interface PatientRepository {

  void save(Patient patient, String passwordHash);

  Optional<Patient> findById(PatientId id);

  Optional<Patient> findByEmail(EmailAddress email);

  Optional<String> findPasswordHashByEmail(EmailAddress email);
}
