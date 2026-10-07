package com.optiflow.platform.production.interfaces.rest.resources;

import java.time.Instant;
import java.time.LocalDate;

public record DeliveryDelayResponse(
    String reason,
    Instant reportedAt,
    LocalDate newEstimatedDeliveryDate) {
}
