package com.optiflow.platform.notification.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
    UUID id,
    UUID patientId,
    @Schema(example = "LENS_ORDER_PROGRESS") String type,
    @Schema(example = "Avance de tu pedido") String title,
    @Schema(example = "Tus lentes están en control de calidad.") String message,
    @Schema(example = "UNREAD", allowableValues = {"UNREAD", "READ"}) String status,
    Instant createdAt,
    @Schema(description = "Date a reminder is sent; null for the other notifications.")
    Instant scheduledAt,
    @Schema(description = "Null while the notification is unread.") Instant readAt,
    @Schema(description = "Appointment of a reminder, if any.") UUID appointmentId,
    @Schema(description = "Work order of an order notification, if any.") UUID workOrderId) {
}
