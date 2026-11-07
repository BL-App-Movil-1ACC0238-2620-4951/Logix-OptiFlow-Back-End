package com.optiflow.platform.searchbooking.infrastructure.security;

import com.optiflow.platform.searchbooking.application.services.AuthenticationPort;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import com.optiflow.platform.shared.security.IssuedJwt;
import com.optiflow.platform.shared.security.JwtTokenService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationAdapter implements AuthenticationPort {

  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  private final JwtTokenService jwtTokenService;

  public AuthenticationAdapter(JwtTokenService jwtTokenService) {
    this.jwtTokenService = jwtTokenService;
  }

  @Override
  public IssuedJwt issueToken(PatientId patientId, String role) {
    return jwtTokenService.issue(patientId.value(), role);
  }

  @Override
  public boolean matches(String rawPassword, String passwordHash) {
    return passwordEncoder.matches(rawPassword, passwordHash);
  }

  @Override
  public String hash(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }
}
