package com.optiflow.platform.searchbooking.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record BookAppointmentRequest(
    @NotNull UUID patientId,
    @NotNull UUID opticalStoreId,
    @NotNull UUID timeSlotId) {
}
