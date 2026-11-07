package com.optiflow.platform.notification.domain.model.events;

import com.optiflow.platform.notification.domain.model.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import com.optiflow.platform.notification.domain.model.valueobjects.WorkOrderId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record LensOrderProgressNotified(
    NotificationId notificationId,
    PatientId patientId,
    WorkOrderId workOrderId,
    String progressSummary,
    Instant occurredAt)
    implements DomainEvent {
}
