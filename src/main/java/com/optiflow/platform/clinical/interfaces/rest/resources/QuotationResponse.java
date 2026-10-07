package com.optiflow.platform.clinical.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record QuotationResponse(
    UUID id,
    UUID clinicalRecordId,
    List<QuotationItemResponse> items,
    DiscountResponse discount,
    BigDecimal subtotal,
    BigDecimal total,
    String currency,
    String status,
    String rejectionReason) {
}
