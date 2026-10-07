package com.optiflow.booking.domain.event;

import com.optiflow.booking.domain.vo.PatientId;
import java.time.Instant;

public record PatientLoggedIn(PatientId patientId, Instant occurredAt) implements DomainEvent {
}
