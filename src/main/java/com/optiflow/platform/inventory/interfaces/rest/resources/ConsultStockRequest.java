package com.optiflow.platform.inventory.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/** Query parameters of {@code GET /api/v1/inventory/stock}. */
public record ConsultStockRequest(
    @Schema(description = "Optical store whose inventory is consulted.",
        example = "11111111-1111-1111-1111-111111111111")
    @NotNull UUID opticalStoreId) {
}
