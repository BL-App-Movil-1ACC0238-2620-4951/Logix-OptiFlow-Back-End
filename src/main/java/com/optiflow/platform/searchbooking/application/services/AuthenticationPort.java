package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;

public interface AuthenticationPort {

  String issueToken(PatientId patientId);

  boolean matches(String rawPassword, String passwordHash);

  String hash(String rawPassword);
}
