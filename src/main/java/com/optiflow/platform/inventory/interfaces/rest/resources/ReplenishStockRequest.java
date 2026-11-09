package com.optiflow.platform.inventory.interfaces.rest.resources;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReplenishStockRequest(
    @NotNull UUID opticalStoreId,
    @NotBlank String sku,
    @NotNull UUID supplierId,
    @NotNull @Min(1) @Max(1000) Integer quantity,
    @Min(0) Integer minimumStock) {
}
