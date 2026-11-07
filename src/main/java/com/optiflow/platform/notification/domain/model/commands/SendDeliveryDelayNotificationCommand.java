package com.optiflow.platform.notification.domain.model.commands;

import java.util.UUID;

public record SendDeliveryDelayNotificationCommand(
    UUID patientId, UUID workOrderId, String reason) {
}
