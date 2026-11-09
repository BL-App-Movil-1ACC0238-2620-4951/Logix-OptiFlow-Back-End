package com.optiflow.platform.inventory.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.UUID;

public record StockResponse(
    UUID frameModelId,
    String sku,
    String name,
    BigDecimal price,
    int quantity,
    int minimumStock,
    boolean low) {
}
