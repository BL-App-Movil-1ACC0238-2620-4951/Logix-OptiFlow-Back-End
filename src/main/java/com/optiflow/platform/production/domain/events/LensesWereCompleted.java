package com.optiflow.platform.production.domain.events;

import com.optiflow.platform.production.domain.valueobjects.LensId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;
import java.util.List;

public record LensesWereCompleted(
    WorkOrderId workOrderId,
    List<LensId> lensIds,
    Instant occurredAt)
    implements DomainEvent {
}
