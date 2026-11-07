package com.optiflow.platform.production.domain.model.events;

import com.optiflow.platform.production.domain.model.valueobjects.PatientId;
import com.optiflow.platform.production.domain.model.valueobjects.SaleId;
import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record OrderWasMarkedAsDelivered(
    WorkOrderId workOrderId,
    SaleId saleId,
    PatientId patientId,
    Instant occurredAt)
    implements DomainEvent {
}
