package com.optiflow.platform.production.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record NotifyDeliveryDelayRequest(
    @NotBlank @Size(max = 255) String reason,
    @NotNull LocalDate newEstimatedDeliveryDate) {
}
