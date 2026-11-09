package com.optiflow.platform.inventory.interfaces.rest.resources;

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
    String currency,
    String tryOnModelUrl,
    Integer quantity,
    Integer minimumStock) {
}
