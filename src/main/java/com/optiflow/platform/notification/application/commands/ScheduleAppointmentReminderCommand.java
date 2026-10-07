package com.optiflow.platform.notification.application.commands;

import java.util.UUID;

public record ScheduleAppointmentReminderCommand(UUID patientId, UUID appointmentId) {
}
