package com.optiflow.platform.searchbooking.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record FavoriteStoreResponse(UUID patientId, UUID opticalStoreId, Instant savedAt) {
}
