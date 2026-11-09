package com.optiflow.platform.clinical.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
    String method,
    BigDecimal amount,
    String transactionReference,
    @Schema(allowableValues = {"APPROVED", "REJECTED"}) String status,
    @Schema(description = "Reason given by the payment gateway, for example "
        + "cc_rejected_insufficient_amount. Empty for cash payments.") String statusDetail,
    Instant paidAt) {
}
