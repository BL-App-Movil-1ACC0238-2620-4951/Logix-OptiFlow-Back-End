package com.optiflow.platform.searchbooking.domain.model.events;

import com.optiflow.platform.searchbooking.domain.model.valueobjects.PatientId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record PatientLoggedIn(PatientId patientId, Instant occurredAt) implements DomainEvent {
}
