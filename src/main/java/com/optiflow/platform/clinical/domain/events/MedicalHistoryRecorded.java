package com.optiflow.platform.clinical.domain.events;

import com.optiflow.platform.clinical.domain.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.valueobjects.PatientId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record MedicalHistoryRecorded(
    ClinicalRecordId clinicalRecordId,
    PatientId patientId,
    Instant occurredAt)
    implements DomainEvent {
}
