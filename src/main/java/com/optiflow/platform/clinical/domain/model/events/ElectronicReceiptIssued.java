package com.optiflow.platform.clinical.domain.model.events;

import com.optiflow.platform.clinical.domain.model.valueobjects.ElectronicReceiptId;
import com.optiflow.platform.clinical.domain.model.valueobjects.Money;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record ElectronicReceiptIssued(
    ElectronicReceiptId receiptId,
    SaleId saleId,
    String receiptNumber,
    Money totalAmount,
    Instant occurredAt)
    implements DomainEvent {
}
