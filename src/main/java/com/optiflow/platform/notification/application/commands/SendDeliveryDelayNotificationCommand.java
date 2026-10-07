package com.optiflow.platform.notification.application.commands;

import java.util.UUID;

public record SendDeliveryDelayNotificationCommand(
    UUID patientId, UUID workOrderId, String reason) {
}
