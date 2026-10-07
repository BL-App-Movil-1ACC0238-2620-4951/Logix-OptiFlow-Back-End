package com.optiflow.platform.searchbooking.domain.events;

import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record OpticalStoresFiltered(String name, String address, Instant occurredAt)
    implements DomainEvent {
}
