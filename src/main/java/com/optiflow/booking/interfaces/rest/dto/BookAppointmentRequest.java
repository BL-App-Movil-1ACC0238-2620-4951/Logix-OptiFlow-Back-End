package com.optiflow.booking.interfaces.rest.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record BookAppointmentRequest(
    @NotNull UUID patientId,
    @NotNull UUID opticalStoreId,
    @NotNull UUID timeSlotId) {
}
