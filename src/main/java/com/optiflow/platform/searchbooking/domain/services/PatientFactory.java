package com.optiflow.platform.searchbooking.domain.services;

import com.optiflow.platform.searchbooking.domain.entities.Patient;
import com.optiflow.platform.searchbooking.domain.valueobjects.EmailAddress;
import com.optiflow.platform.searchbooking.domain.valueobjects.Name;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.domain.valueobjects.PhoneNumber;
import java.time.Instant;
import java.time.LocalDate;

public class PatientFactory {

  public Patient create(
      Name name, EmailAddress email, PhoneNumber phone, LocalDate birthDate, Instant now) {
    return Patient.register(PatientId.generate(), name, email, phone, birthDate, now);
  }
}
