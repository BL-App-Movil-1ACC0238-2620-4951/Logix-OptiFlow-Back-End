package com.optiflow.platform.production.domain.model.events;

import com.optiflow.platform.production.domain.model.valueobjects.TechnicianId;
import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record WorkOrderAssigned(
    WorkOrderId workOrderId,
    TechnicianId technicianId,
    Instant occurredAt)
    implements DomainEvent {
}
