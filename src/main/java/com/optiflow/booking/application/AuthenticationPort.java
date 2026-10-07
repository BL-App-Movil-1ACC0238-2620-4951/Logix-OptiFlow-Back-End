package com.optiflow.booking.application;

import com.optiflow.booking.domain.vo.PatientId;

public interface AuthenticationPort {

  String issueToken(PatientId patientId);

  boolean matches(String rawPassword, String passwordHash);

  String hash(String rawPassword);
}
