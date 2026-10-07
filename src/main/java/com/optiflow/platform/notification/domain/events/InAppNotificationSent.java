package com.optiflow.platform.notification.domain.events;

import com.optiflow.platform.notification.domain.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.valueobjects.NotificationType;
import com.optiflow.platform.notification.domain.valueobjects.PatientId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record InAppNotificationSent(
    NotificationId notificationId,
    PatientId patientId,
    NotificationType type,
    Instant occurredAt)
    implements DomainEvent {
}
