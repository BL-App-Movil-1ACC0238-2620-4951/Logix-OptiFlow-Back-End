package com.optiflow.booking.domain.event;

import com.optiflow.booking.domain.vo.OpticalStoreId;
import java.time.Instant;

public record OpticalStoresPublished(OpticalStoreId opticalStoreId, Instant occurredAt)
    implements DomainEvent {
}
