package com.optiflow.platform.clinical.domain.model.events;

import com.optiflow.platform.clinical.domain.model.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record QuotationRejected(
    QuotationId quotationId,
    ClinicalRecordId clinicalRecordId,
    String reason,
    Instant occurredAt)
    implements DomainEvent {
}
