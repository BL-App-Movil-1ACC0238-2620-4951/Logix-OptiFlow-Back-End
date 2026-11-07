package com.optiflow.platform.shared.infrastructure.security;

import java.time.Instant;

public record IssuedJwt(String token, Instant expiresAt) {}
