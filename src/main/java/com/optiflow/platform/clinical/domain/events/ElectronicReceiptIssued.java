package com.optiflow.platform.clinical.domain.events;

import com.optiflow.platform.clinical.domain.valueobjects.ElectronicReceiptId;
import com.optiflow.platform.clinical.domain.valueobjects.Money;
import com.optiflow.platform.clinical.domain.valueobjects.SaleId;
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
