package com.optiflow.platform.clinical.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RegisterSaleRequest(
    @NotNull UUID quotationId) {
}
