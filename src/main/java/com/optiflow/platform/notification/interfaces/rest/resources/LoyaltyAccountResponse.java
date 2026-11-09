package com.optiflow.platform.notification.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

public record LoyaltyAccountResponse(
    UUID patientId,
    @Schema(example = "100") int balance,
    @Schema(example = "2026", description = "Null until the first birthday discount.")
    Integer lastBirthdayDiscountYear,
    Instant updatedAt) {
}
