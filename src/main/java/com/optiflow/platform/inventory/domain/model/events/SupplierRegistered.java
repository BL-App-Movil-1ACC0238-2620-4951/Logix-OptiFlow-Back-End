package com.optiflow.platform.inventory.domain.model.events;

import com.optiflow.platform.inventory.domain.model.valueobjects.SupplierId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record SupplierRegistered(SupplierId supplierId, String name, Instant occurredAt) implements DomainEvent {
}
