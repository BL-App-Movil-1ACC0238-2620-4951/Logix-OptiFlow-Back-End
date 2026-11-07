package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import com.optiflow.platform.shared.security.IssuedJwt;

public interface AuthenticationPort {

  IssuedJwt issueToken(PatientId patientId, String role);

  boolean matches(String rawPassword, String passwordHash);

  String hash(String rawPassword);
}
