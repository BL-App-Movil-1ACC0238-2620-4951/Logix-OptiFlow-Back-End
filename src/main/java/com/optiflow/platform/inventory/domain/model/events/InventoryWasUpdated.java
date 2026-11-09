package com.optiflow.platform.inventory.domain.model.events;

import com.optiflow.platform.inventory.domain.model.valueobjects.InventoryId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record InventoryWasUpdated(InventoryId inventoryId, UUID saleId, Instant occurredAt) implements DomainEvent {
}
