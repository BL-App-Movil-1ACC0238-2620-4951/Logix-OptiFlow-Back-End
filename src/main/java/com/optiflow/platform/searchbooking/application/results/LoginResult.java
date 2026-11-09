package com.optiflow.platform.searchbooking.application.results;

import java.time.Instant;
import java.util.UUID;

public record LoginResult(String token, Instant expiresAt, UUID userId, String role) {
}
