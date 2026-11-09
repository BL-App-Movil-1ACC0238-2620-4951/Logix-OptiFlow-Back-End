package com.optiflow.platform.clinical.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateSaleStatusRequest(
    @Schema(allowableValues = "CLOSED") @NotBlank String status) {
}
