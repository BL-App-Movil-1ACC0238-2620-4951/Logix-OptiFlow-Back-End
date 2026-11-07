package com.optiflow.platform.production.domain.model.events;

import com.optiflow.platform.production.domain.model.valueobjects.DeliveryDelay;
import com.optiflow.platform.production.domain.model.valueobjects.PatientId;
import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record DeliveryDelayNotified(
    WorkOrderId workOrderId,
    PatientId patientId,
    DeliveryDelay delay,
    Instant occurredAt)
    implements DomainEvent {
}
