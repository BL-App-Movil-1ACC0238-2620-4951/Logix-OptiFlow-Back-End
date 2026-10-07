package com.optiflow.platform.searchbooking.interfaces.rest.resources;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record RateOpticalStoreRequest(
    @NotNull UUID patientId,
    @NotNull @DecimalMin("1.00") @DecimalMax("5.00") BigDecimal score,
    @Size(max = 280) String comment) {
}
