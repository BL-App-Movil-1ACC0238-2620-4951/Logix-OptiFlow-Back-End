package com.optiflow.platform.shared.security;

import java.time.Instant;

public record IssuedJwt(String token, Instant expiresAt) {}
