package com.optiflow.platform.searchbooking.domain.events;

import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record OpticalStoresPublished(OpticalStoreId opticalStoreId, Instant occurredAt)
    implements DomainEvent {
}
