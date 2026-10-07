package com.optiflow.platform.searchbooking.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record TimeSlotResponse(
    UUID id, UUID opticalStoreId, Instant startDateTime, Instant endDateTime, String status) {
}
