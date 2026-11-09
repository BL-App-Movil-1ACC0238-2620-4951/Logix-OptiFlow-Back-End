package com.optiflow.platform.notification.domain.model.commands;

import java.util.UUID;

public record SendSatisfactionSurveyCommand(UUID patientId, UUID workOrderId) {
}
