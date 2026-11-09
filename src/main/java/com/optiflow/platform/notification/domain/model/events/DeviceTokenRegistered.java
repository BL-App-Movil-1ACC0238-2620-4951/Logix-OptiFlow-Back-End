package com.optiflow.platform.notification.domain.model.events;

import com.optiflow.platform.notification.domain.model.valueobjects.DevicePlatform;
import com.optiflow.platform.notification.domain.model.valueobjects.DeviceTokenId;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record DeviceTokenRegistered(
    DeviceTokenId deviceTokenId,
    PatientId patientId,
    DevicePlatform platform,
    Instant occurredAt)
    implements DomainEvent {
}
