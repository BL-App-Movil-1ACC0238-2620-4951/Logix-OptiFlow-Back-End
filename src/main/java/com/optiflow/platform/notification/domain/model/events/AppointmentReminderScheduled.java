package com.optiflow.platform.notification.domain.model.events;

import com.optiflow.platform.notification.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record AppointmentReminderScheduled(
    AppointmentId appointmentId,
    PatientId patientId,
    Instant reminderAt,
    Instant annualCheckupAt,
    Instant occurredAt)
    implements DomainEvent {
}
