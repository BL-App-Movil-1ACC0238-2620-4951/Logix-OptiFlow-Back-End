package com.optiflow.platform.shared.infrastructure.security;

import java.util.UUID;

public record AuthenticatedUser(UUID userId, String role) {}
