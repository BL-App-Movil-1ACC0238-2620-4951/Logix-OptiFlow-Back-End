package com.optiflow.platform.searchbooking.domain.factories;

import com.optiflow.platform.searchbooking.domain.model.entities.Patient;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.EmailAddress;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.Name;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PhoneNumber;
import java.time.Instant;
import java.time.LocalDate;

public class PatientFactory {

  public Patient create(
      Name name, EmailAddress email, PhoneNumber phone, LocalDate birthDate, Instant now) {
    return Patient.register(PatientId.generate(), name, email, phone, birthDate, now);
  }
}
