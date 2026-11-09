package com.optiflow.platform.clinical.domain.model.events;

import com.optiflow.platform.clinical.domain.model.valueobjects.Discount;
import com.optiflow.platform.clinical.domain.model.valueobjects.Money;
import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record PromotionOrDiscountApplied(
    QuotationId quotationId,
    Discount discount,
    Money total,
    Instant occurredAt)
    implements DomainEvent {
}
