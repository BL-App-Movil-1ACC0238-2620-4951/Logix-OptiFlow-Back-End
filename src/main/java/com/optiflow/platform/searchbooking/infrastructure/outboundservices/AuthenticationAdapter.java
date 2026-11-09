package com.optiflow.platform.searchbooking.infrastructure.outboundservices;

import com.optiflow.platform.searchbooking.application.outboundservices.AuthenticationPort;
import com.optiflow.platform.shared.infrastructure.security.IssuedJwt;
import com.optiflow.platform.shared.infrastructure.security.JwtTokenService;
import java.util.UUID;
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
  public IssuedJwt issueToken(UUID userId, String role) {
    return jwtTokenService.issue(userId, role);
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
