package com.optiflow.platform.searchbooking.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record StaffMemberResponse(
    UUID id,
    UUID opticalStoreId,
    String name,
    String email,
    String role,
    boolean active,
    Instant createdAt) {}
