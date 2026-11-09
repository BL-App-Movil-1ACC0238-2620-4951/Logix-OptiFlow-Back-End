package com.optiflow.platform.clinical.domain.model.events;

import com.optiflow.platform.clinical.domain.model.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.model.valueobjects.PatientId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record OpticalPrescriptionGenerated(
    ClinicalRecordId clinicalRecordId,
    PatientId patientId,
    Instant occurredAt)
    implements DomainEvent {
}
