package com.optiflow.platform.production.interfaces.rest.resources;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record WorkOrderResponse(
    UUID id,
    UUID saleId,
    UUID patientId,
    UUID opticalStoreId,
    String status,
    LocalDate estimatedDeliveryDate,
    boolean delayed,
    TechnicianResponse technician,
    LaboratoryResponse laboratory,
    List<LensResponse> lenses,
    DeliveryDelayResponse deliveryDelay,
    List<StatusChangeResponse> statusHistory,
    Instant createdAt,
    Instant updatedAt,
    Instant deliveredAt) {
}
