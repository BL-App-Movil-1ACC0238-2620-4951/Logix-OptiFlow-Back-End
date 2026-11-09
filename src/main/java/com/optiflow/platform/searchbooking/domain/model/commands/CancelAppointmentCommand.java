package com.optiflow.platform.searchbooking.domain.model.commands;

import com.optiflow.platform.searchbooking.domain.model.valueobjects.AppointmentId;

public record CancelAppointmentCommand(AppointmentId appointmentId) {}
