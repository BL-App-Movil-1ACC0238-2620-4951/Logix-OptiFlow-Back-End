package com.optiflow.platform.searchbooking.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.UUID;

public record StoreRatingResponse(
    UUID patientId, UUID opticalStoreId, BigDecimal score, BigDecimal averageRating) {
}
