package com.optiflow.platform.production.domain.events;

import com.optiflow.platform.production.domain.valueobjects.LaboratoryId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record WorkOrderSentToLaboratory(
    WorkOrderId workOrderId,
    LaboratoryId laboratoryId,
    Instant occurredAt)
    implements DomainEvent {
}
