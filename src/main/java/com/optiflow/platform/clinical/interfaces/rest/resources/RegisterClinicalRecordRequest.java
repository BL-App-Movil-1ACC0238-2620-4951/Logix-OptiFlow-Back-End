package com.optiflow.platform.clinical.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

public record RegisterClinicalRecordRequest(
    @NotNull UUID patientId,
    @NotNull UUID appointmentId,
    Instant examinationDate,
    @Size(max = 500) String observations) {
}
