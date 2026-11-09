package com.optiflow.platform.production.domain.model.events;

import com.optiflow.platform.production.domain.model.valueobjects.LensId;
import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;
import java.util.List;

public record LensesWereCompleted(
    WorkOrderId workOrderId,
    List<LensId> lensIds,
    Instant occurredAt)
    implements DomainEvent {
}
