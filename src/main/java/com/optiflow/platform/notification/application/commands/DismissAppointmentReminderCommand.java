package com.optiflow.platform.notification.application.commands;

import java.util.UUID;

public record DismissAppointmentReminderCommand(UUID patientId, UUID appointmentId) {}
