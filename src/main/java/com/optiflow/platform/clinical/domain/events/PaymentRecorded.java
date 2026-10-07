package com.optiflow.platform.clinical.domain.events;

import com.optiflow.platform.clinical.domain.valueobjects.Money;
import com.optiflow.platform.clinical.domain.valueobjects.PaymentMethod;
import com.optiflow.platform.clinical.domain.valueobjects.SaleId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record PaymentRecorded(
    SaleId saleId,
    PaymentMethod method,
    Money amount,
    Instant occurredAt)
    implements DomainEvent {
}
