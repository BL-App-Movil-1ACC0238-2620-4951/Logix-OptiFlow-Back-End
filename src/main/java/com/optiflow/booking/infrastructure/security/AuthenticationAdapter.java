package com.optiflow.booking.infrastructure.security;

import com.optiflow.booking.application.AuthenticationPort;
import com.optiflow.booking.domain.vo.PatientId;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationAdapter implements AuthenticationPort {

  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  private final Map<String, PatientId> tokens = new ConcurrentHashMap<>();

  @Override
  public String issueToken(PatientId patientId) {
    String token = UUID.randomUUID().toString();
    tokens.put(token, patientId);
    return token;
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
