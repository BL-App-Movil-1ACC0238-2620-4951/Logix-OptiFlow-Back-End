package com.optiflow.booking.interfaces.rest.dto;

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
