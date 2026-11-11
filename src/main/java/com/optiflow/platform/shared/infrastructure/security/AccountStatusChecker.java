package com.optiflow.platform.shared.infrastructure.security;

import java.util.UUID;

/** Tells the JWT filter whether the account behind a token can still use the API. */
public interface AccountStatusChecker {

  boolean isActive(UUID userId, String role);
}
