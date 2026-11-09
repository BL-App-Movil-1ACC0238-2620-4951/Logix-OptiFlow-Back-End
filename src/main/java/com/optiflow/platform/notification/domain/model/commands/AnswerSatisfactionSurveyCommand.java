package com.optiflow.platform.notification.domain.model.commands;

import java.util.UUID;

public record AnswerSatisfactionSurveyCommand(UUID notificationId, int rating, String comment) {
}
