package com.optiflow.booking.interfaces.rest.dto;

import java.time.Instant;
import java.util.UUID;

public record FavoriteStoreResponse(UUID patientId, UUID opticalStoreId, Instant savedAt) {
}
