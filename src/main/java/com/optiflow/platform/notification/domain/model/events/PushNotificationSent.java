package com.optiflow.platform.notification.domain.model.events;

import com.optiflow.platform.notification.domain.model.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record PushNotificationSent(
    NotificationId notificationId,
    PatientId patientId,
    int deliveredDevices,
    Instant occurredAt)
    implements DomainEvent {
}
