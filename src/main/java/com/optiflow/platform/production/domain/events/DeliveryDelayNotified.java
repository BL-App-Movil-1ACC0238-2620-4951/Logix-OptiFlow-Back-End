package com.optiflow.platform.production.domain.events;

import com.optiflow.platform.production.domain.valueobjects.DeliveryDelay;
import com.optiflow.platform.production.domain.valueobjects.PatientId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record DeliveryDelayNotified(
    WorkOrderId workOrderId,
    PatientId patientId,
    DeliveryDelay delay,
    Instant occurredAt)
    implements DomainEvent {
}
