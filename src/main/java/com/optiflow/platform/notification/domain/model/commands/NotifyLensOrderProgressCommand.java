package com.optiflow.platform.notification.domain.model.commands;

import java.util.UUID;

public record NotifyLensOrderProgressCommand(
    UUID patientId, UUID workOrderId, String progressSummary) {
}
