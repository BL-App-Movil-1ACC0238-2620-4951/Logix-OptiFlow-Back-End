package com.optiflow.platform.notification.domain.model.commands;

import java.util.UUID;

public record ScheduleAppointmentReminderCommand(UUID patientId, UUID appointmentId) {
}
