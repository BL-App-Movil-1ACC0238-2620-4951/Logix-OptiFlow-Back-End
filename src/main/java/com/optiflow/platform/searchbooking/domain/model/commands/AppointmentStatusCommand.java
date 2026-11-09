package com.optiflow.platform.searchbooking.domain.model.commands;

/** Command that changes the status of an appointment. */
public sealed interface AppointmentStatusCommand
    permits CancelAppointmentCommand, CompleteAppointmentCommand {}
