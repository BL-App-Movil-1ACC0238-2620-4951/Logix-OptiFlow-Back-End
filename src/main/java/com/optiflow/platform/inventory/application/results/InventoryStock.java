package com.optiflow.platform.inventory.application.results;

import com.optiflow.platform.inventory.domain.model.aggregates.Inventory;
import java.util.List;

public record InventoryStock(Inventory inventory, List<StockLevel> levels) {
}
