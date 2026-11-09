package com.optiflow.platform.production.domain.model.events;

import com.optiflow.platform.production.domain.model.valueobjects.LaboratoryId;
import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record WorkOrderSentToLaboratory(
    WorkOrderId workOrderId,
    LaboratoryId laboratoryId,
    Instant occurredAt)
    implements DomainEvent {
}
