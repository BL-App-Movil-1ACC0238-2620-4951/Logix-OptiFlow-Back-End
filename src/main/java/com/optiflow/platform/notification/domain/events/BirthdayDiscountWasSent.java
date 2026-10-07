package com.optiflow.platform.notification.domain.events;

import com.optiflow.platform.notification.domain.valueobjects.LoyaltyPoints;
import com.optiflow.platform.notification.domain.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.valueobjects.PatientId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record BirthdayDiscountWasSent(
    NotificationId notificationId,
    PatientId patientId,
    LoyaltyPoints bonusPoints,
    int calendarYear,
    Instant occurredAt)
    implements DomainEvent {
}
