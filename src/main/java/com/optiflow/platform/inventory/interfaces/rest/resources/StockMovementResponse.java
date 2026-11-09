package com.optiflow.platform.inventory.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record StockMovementResponse(
    UUID id,
    Instant createdAt,
    String sku,
    String frameModelName,
    String type,
    String reason,
    int quantity,
    UUID saleId,
    UUID supplierId,
    UUID performedBy) {
}
