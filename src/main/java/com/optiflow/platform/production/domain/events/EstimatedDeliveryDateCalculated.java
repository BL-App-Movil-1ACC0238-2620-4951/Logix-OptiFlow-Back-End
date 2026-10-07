package com.optiflow.platform.production.domain.events;

import com.optiflow.platform.production.domain.valueobjects.DeliveryDate;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record EstimatedDeliveryDateCalculated(
    WorkOrderId workOrderId,
    DeliveryDate estimatedDeliveryDate,
    Instant occurredAt)
    implements DomainEvent {
}
