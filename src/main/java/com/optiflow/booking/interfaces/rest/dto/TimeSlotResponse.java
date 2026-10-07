package com.optiflow.booking.interfaces.rest.dto;

import java.time.Instant;
import java.util.UUID;

public record TimeSlotResponse(
    UUID id, UUID opticalStoreId, Instant startDateTime, Instant endDateTime, String status) {
}
