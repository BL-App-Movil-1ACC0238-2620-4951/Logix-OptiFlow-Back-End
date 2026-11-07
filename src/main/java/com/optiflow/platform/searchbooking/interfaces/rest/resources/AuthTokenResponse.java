package com.optiflow.platform.searchbooking.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record AuthTokenResponse(String token, Instant expiresAt, UUID userId, String role) {}
