package com.optiflow.platform.inventory.domain.model.events;

import com.optiflow.platform.inventory.domain.model.valueobjects.FrameModelId;
import com.optiflow.platform.inventory.domain.model.valueobjects.Sku;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record NewFrameModelAdded(FrameModelId frameModelId, Sku sku, Instant occurredAt) implements DomainEvent {
}
