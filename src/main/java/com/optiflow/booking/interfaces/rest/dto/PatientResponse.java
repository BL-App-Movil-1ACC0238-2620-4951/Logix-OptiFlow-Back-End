package com.optiflow.booking.interfaces.rest.dto;

import java.time.Instant;
import java.util.UUID;

public record PatientResponse(
    UUID id, String name, String email, String phone, Instant createdAt) {
}
