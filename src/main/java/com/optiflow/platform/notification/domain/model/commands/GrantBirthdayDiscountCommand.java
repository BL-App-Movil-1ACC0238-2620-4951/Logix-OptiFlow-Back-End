package com.optiflow.platform.notification.domain.model.commands;

import java.util.UUID;

public record GrantBirthdayDiscountCommand(UUID patientId, int calendarYear) {
}
