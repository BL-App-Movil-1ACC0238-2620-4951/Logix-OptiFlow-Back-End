package com.optiflow.platform.notification.application.commands;

import java.util.UUID;

public record NotifyLensOrderProgressCommand(
    UUID patientId, UUID workOrderId, String progressSummary) {
}
