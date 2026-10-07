package com.optiflow.platform.searchbooking.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record PatientResponse(
    UUID id, String name, String email, String phone, Instant createdAt) {
}
