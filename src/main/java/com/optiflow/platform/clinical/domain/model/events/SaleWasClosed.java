package com.optiflow.platform.clinical.domain.model.events;

import com.optiflow.platform.clinical.domain.model.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.model.valueobjects.Money;
import com.optiflow.platform.clinical.domain.model.valueobjects.PatientId;
import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationId;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleId;
import com.optiflow.platform.clinical.domain.model.valueobjects.SoldProduct;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Published when a paid sale is closed.
 *
 * @param opticalStoreId store where the patient was attended; {@code null} when it cannot be found
 * @param soldProducts quotation items that have a product SKU
 */
public record SaleWasClosed(
    SaleId saleId,
    QuotationId quotationId,
    ClinicalRecordId clinicalRecordId,
    PatientId patientId,
    Money totalAmount,
    UUID opticalStoreId,
    List<SoldProduct> soldProducts,
    Instant occurredAt)
    implements DomainEvent {
}
