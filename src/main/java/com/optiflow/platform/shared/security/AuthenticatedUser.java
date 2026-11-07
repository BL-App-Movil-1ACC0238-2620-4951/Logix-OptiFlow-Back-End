package com.optiflow.platform.shared.security;

import java.util.UUID;

public record AuthenticatedUser(UUID userId, String role) {}
