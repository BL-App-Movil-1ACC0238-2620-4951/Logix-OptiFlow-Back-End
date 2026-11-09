package com.optiflow.platform.inventory.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

public record StockMovementResponse(
    UUID id,
    Instant createdAt,
    String sku,
    String frameModelName,
    @Schema(allowableValues = {"IN", "OUT"}) String type,
    @Schema(allowableValues = {"REPLENISHMENT", "SALE", "ADJUSTMENT"}) String reason,
    int quantity,
    @Schema(description = "Only filled in the exits of a sale.") UUID saleId,
    @Schema(description = "Only filled in replenishments.") UUID supplierId,
    @Schema(description = "Staff member who registered it; empty in the exits of a sale.")
    UUID performedBy) {
}
