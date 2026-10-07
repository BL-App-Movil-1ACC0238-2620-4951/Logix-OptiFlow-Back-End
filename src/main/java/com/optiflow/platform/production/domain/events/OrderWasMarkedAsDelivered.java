package com.optiflow.platform.production.domain.events;

import com.optiflow.platform.production.domain.valueobjects.PatientId;
import com.optiflow.platform.production.domain.valueobjects.SaleId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record OrderWasMarkedAsDelivered(
    WorkOrderId workOrderId,
    SaleId saleId,
    PatientId patientId,
    Instant occurredAt)
    implements DomainEvent {
}
