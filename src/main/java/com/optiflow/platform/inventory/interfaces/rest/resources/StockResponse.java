package com.optiflow.platform.inventory.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

public record StockResponse(
    UUID frameModelId,
    String sku,
    String name,
    BigDecimal price,
    int quantity,
    int minimumStock,
    @Schema(description = "True when the quantity is at or below the minimum stock.") boolean low) {
}
