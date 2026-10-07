package com.optiflow.booking.domain.event;

import java.time.Instant;

public record OpticalStoresFiltered(String name, String address, Instant occurredAt)
    implements DomainEvent {
}
