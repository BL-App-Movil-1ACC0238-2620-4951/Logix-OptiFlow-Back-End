package com.optiflow.platform.production.domain.events;

import com.optiflow.platform.production.domain.valueobjects.PatientId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderStatus;
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
