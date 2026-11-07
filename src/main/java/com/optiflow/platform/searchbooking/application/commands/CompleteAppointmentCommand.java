package com.optiflow.platform.searchbooking.application.commands;

import com.optiflow.platform.searchbooking.domain.valueobjects.AppointmentId;

public record CompleteAppointmentCommand(AppointmentId appointmentId) {}
