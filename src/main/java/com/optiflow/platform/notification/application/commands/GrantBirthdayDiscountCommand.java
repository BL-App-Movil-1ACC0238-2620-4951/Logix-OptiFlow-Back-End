package com.optiflow.platform.notification.application.commands;

import java.util.UUID;

public record GrantBirthdayDiscountCommand(UUID patientId, int calendarYear) {
}
