package com.optiflow.platform.inventory.domain.model.commands;

import com.optiflow.platform.inventory.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.inventory.domain.model.valueobjects.Sku;
import com.optiflow.platform.inventory.domain.model.valueobjects.SupplierId;
import java.util.UUID;

public record ReplenishStockCommand(OpticalStoreId opticalStoreId, Sku sku, SupplierId supplierId, int quantity, Integer minimumStock, UUID performedBy) {
}
