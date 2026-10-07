package com.optiflow.booking.interfaces.rest.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record StoreRatingResponse(
    UUID patientId, UUID opticalStoreId, BigDecimal score, BigDecimal averageRating) {
}
