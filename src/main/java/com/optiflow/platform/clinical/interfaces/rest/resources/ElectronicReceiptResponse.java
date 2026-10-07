package com.optiflow.platform.clinical.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ElectronicReceiptResponse(
    UUID id,
    String receiptNumber,
    Instant issueDate,
    BigDecimal taxAmount,
    BigDecimal totalAmount) {
}
