package com.optiflow.platform.production.domain.events;

import com.optiflow.platform.production.domain.valueobjects.TechnicianId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record WorkOrderAssigned(
    WorkOrderId workOrderId,
    TechnicianId technicianId,
    Instant occurredAt)
    implements DomainEvent {
}
