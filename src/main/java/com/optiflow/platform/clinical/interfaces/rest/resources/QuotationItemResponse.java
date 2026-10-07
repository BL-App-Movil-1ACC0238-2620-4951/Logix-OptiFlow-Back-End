package com.optiflow.platform.clinical.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.UUID;

public record QuotationItemResponse(
    UUID id,
    String itemType,
    String productSku,
    String description,
    BigDecimal unitPrice,
    int quantity,
    BigDecimal subtotal) {
}
