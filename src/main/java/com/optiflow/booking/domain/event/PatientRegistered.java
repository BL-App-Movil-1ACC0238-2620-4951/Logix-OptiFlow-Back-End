package com.optiflow.booking.domain.event;

import com.optiflow.booking.domain.vo.EmailAddress;
import com.optiflow.booking.domain.vo.PatientId;
import java.time.Instant;

public record PatientRegistered(PatientId patientId, EmailAddress email, Instant occurredAt)
    implements DomainEvent {
}
