package com.optiflow.platform.production.domain.model.events;

import com.optiflow.platform.production.domain.model.valueobjects.DeliveryDate;
import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record EstimatedDeliveryDateCalculated(
    WorkOrderId workOrderId,
    DeliveryDate estimatedDeliveryDate,
    Instant occurredAt)
    implements DomainEvent {
}
