package com.optiflow.platform.clinical.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record SaleResponse(
    UUID id,
    UUID quotationId,
    UUID patientId,
    String status,
    Instant closedAt,
    PaymentResponse payment,
    ElectronicReceiptResponse receipt) {
}
