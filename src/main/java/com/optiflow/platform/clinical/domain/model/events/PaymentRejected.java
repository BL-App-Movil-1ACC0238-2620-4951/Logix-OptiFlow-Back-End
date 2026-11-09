package com.optiflow.platform.clinical.domain.model.events;

import com.optiflow.platform.clinical.domain.model.valueobjects.Money;
import com.optiflow.platform.clinical.domain.model.valueobjects.PaymentMethod;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

/** The payment gateway rejected a payment; the sale stays pending payment. */
public record PaymentRejected(
    SaleId saleId,
    PaymentMethod method,
    Money amount,
    String statusDetail,
    Instant occurredAt)
    implements DomainEvent {
}
