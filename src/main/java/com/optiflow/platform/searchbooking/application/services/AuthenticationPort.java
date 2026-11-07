package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.shared.security.IssuedJwt;
import java.util.UUID;

public interface AuthenticationPort {

  IssuedJwt issueToken(UUID userId, String role);

  boolean matches(String rawPassword, String passwordHash);

  String hash(String rawPassword);
}
