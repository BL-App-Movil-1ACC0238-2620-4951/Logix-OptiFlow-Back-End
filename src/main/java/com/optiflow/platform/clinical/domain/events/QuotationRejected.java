package com.optiflow.platform.clinical.domain.events;

import com.optiflow.platform.clinical.domain.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.valueobjects.QuotationId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record QuotationRejected(
    QuotationId quotationId,
    ClinicalRecordId clinicalRecordId,
    String reason,
    Instant occurredAt)
    implements DomainEvent {
}
