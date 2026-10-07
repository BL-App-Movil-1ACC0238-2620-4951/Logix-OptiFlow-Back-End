package com.optiflow.platform.clinical.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record GenerateOpticalPrescriptionRequest(
    @NotNull BigDecimal sphereOD,
    @NotNull BigDecimal cylinderOD,
    @NotNull Integer axisOD,
    @NotNull BigDecimal sphereOS,
    @NotNull BigDecimal cylinderOS,
    @NotNull Integer axisOS,
    BigDecimal addition,
    @Size(max = 255) String treatment,
    @Size(max = 255) String recommendedFrameType) {
}
