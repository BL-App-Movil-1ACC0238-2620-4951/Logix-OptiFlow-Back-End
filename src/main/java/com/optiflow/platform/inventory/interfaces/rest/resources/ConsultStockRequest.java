package com.optiflow.platform.inventory.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/** Query parameters of {@code GET /api/v1/inventory/stock}. */
public record ConsultStockRequest(
    @NotNull UUID opticalStoreId) {
}
