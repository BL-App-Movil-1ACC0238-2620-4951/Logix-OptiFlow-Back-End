package com.optiflow.platform.notification.domain.events;

import com.optiflow.platform.notification.domain.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.valueobjects.PatientId;
import com.optiflow.platform.notification.domain.valueobjects.WorkOrderId;
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
