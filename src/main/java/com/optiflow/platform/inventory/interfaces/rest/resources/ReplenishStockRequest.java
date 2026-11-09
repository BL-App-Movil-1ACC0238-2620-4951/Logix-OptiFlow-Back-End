package com.optiflow.platform.inventory.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReplenishStockRequest(
    @Schema(example = "11111111-1111-1111-1111-111111111111") @NotNull UUID opticalStoreId,
    @Schema(example = "RB-2140-BLK") @NotBlank String sku,
    @Schema(description = "Supplier that delivered the frames.") @NotNull UUID supplierId,
    @Schema(example = "10") @NotNull @Min(1) @Max(1000) Integer quantity,
    @Schema(description = "New minimum stock of the frame in the store. When omitted, a new "
        + "stock starts with 5 and an existing one keeps its minimum.", example = "5")
    @Min(0) Integer minimumStock) {
}
