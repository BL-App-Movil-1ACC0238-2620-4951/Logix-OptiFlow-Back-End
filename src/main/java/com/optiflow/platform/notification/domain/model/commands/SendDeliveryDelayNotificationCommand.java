package com.optiflow.platform.notification.domain.model.commands;

import java.time.LocalDate;
import java.util.UUID;

public record SendDeliveryDelayNotificationCommand(
    UUID patientId, UUID workOrderId, String reason, LocalDate newEstimatedDeliveryDate) {
}
