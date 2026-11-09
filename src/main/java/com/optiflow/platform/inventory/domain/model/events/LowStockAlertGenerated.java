package com.optiflow.platform.inventory.domain.model.events;

import com.optiflow.platform.inventory.domain.model.valueobjects.FrameModelId;
import com.optiflow.platform.inventory.domain.model.valueobjects.InventoryId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record LowStockAlertGenerated(InventoryId inventoryId, FrameModelId frameModelId, int quantity, int minimumStock, Instant occurredAt) implements DomainEvent {
}
