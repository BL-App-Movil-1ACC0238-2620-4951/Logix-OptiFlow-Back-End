package com.optiflow.platform.clinical.domain.model.events;

import com.optiflow.platform.clinical.domain.model.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.model.valueobjects.Money;
import com.optiflow.platform.clinical.domain.model.valueobjects.PatientId;
import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationId;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleId;
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
