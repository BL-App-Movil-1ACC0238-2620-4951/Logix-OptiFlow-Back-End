package com.optiflow.platform.inventory.domain.model.events;

import com.optiflow.platform.inventory.domain.model.valueobjects.FrameModelId;
import com.optiflow.platform.inventory.domain.model.valueobjects.Price;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record FrameModelPriceUpdated(FrameModelId frameModelId, Price price, Instant occurredAt) implements DomainEvent {
}
