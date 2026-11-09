package com.optiflow.platform.inventory.interfaces.rest.resources;

import java.util.UUID;

public record LowStockAlertResponse(
    UUID frameModelId, String sku, String name, int quantity, int minimumStock) {
}
