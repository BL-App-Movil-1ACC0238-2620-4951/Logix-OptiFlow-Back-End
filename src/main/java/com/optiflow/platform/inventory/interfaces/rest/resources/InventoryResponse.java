package com.optiflow.platform.inventory.interfaces.rest.resources;

import java.util.List;
import java.util.UUID;

public record InventoryResponse(
    UUID id, UUID opticalStoreId, List<StockResponse> stocks) {
}
