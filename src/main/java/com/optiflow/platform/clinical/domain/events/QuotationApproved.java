package com.optiflow.platform.clinical.domain.events;

import com.optiflow.platform.clinical.domain.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.valueobjects.Money;
import com.optiflow.platform.clinical.domain.valueobjects.QuotationId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record QuotationApproved(
    QuotationId quotationId,
    ClinicalRecordId clinicalRecordId,
    Money total,
    Instant occurredAt)
    implements DomainEvent {
}
