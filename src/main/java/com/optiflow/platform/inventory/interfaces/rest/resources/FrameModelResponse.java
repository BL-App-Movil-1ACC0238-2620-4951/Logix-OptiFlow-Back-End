package com.optiflow.platform.inventory.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

/** {@code quantity} and {@code minimumStock} are only filled when a store is requested. */
public record FrameModelResponse(
    UUID id,
    String sku,
    String name,
    String material,
    String shape,
    String color,
    BigDecimal price,
    @Schema(example = "PEN") String currency,
    String tryOnModelUrl,
    @Schema(description = "Only filled when opticalStoreId is given.") Integer quantity,
    @Schema(description = "Only filled when opticalStoreId is given.") Integer minimumStock) {
}
