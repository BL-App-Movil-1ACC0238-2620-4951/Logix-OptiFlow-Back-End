package com.optiflow.platform.production.domain.model.events;

import com.optiflow.platform.production.domain.model.valueobjects.PatientId;
import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderId;
import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderStatus;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record WorkOrderStatusUpdated(
    WorkOrderId workOrderId,
    PatientId patientId,
    WorkOrderStatus previousStatus,
    WorkOrderStatus status,
    Instant occurredAt)
    implements DomainEvent {
}
