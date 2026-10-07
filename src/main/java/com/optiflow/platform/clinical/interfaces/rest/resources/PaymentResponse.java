package com.optiflow.platform.clinical.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
    String method,
    BigDecimal amount,
    String transactionReference,
    Instant paidAt) {
}
