package com.optiflow.booking.domain.factory;

import com.optiflow.booking.domain.model.Patient;
import com.optiflow.booking.domain.vo.EmailAddress;
import com.optiflow.booking.domain.vo.Name;
import com.optiflow.booking.domain.vo.PatientId;
import com.optiflow.booking.domain.vo.PhoneNumber;
import java.time.Instant;

public class PatientFactory {

  public Patient create(Name name, EmailAddress email, PhoneNumber phone, Instant now) {
    return Patient.register(PatientId.generate(), name, email, phone, now);
  }
}
