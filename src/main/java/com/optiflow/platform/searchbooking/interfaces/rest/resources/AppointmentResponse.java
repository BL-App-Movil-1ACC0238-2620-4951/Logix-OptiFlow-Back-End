package com.optiflow.platform.searchbooking.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record AppointmentResponse(
    UUID id,
    UUID patientId,
    UUID opticalStoreId,
    UUID timeSlotId,
    String status,
    Instant startDateTime,
    Instant endDateTime,
    Instant createdAt,
    Instant updatedAt) {
}
