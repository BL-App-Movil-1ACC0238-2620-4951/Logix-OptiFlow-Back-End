package com.optiflow.platform.clinical.domain.events;

import com.optiflow.platform.clinical.domain.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.valueobjects.Money;
import com.optiflow.platform.clinical.domain.valueobjects.PatientId;
import com.optiflow.platform.clinical.domain.valueobjects.QuotationId;
import com.optiflow.platform.clinical.domain.valueobjects.SaleId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record SaleWasClosed(
    SaleId saleId,
    QuotationId quotationId,
    ClinicalRecordId clinicalRecordId,
    PatientId patientId,
    Money totalAmount,
    Instant occurredAt)
    implements DomainEvent {
}
