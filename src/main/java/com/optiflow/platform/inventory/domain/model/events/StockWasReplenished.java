package com.optiflow.platform.inventory.domain.model.events;

import com.optiflow.platform.inventory.domain.model.valueobjects.FrameModelId;
import com.optiflow.platform.inventory.domain.model.valueobjects.InventoryId;
import com.optiflow.platform.inventory.domain.model.valueobjects.SupplierId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record StockWasReplenished(InventoryId inventoryId, FrameModelId frameModelId, SupplierId supplierId, int quantity, Instant occurredAt) implements DomainEvent {
}
