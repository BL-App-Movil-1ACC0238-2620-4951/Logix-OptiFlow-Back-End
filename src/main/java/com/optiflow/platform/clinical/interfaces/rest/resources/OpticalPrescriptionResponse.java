package com.optiflow.platform.clinical.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.UUID;

public record OpticalPrescriptionResponse(
    UUID clinicalRecordId,
    BigDecimal sphereOD,
    BigDecimal cylinderOD,
    Integer axisOD,
    BigDecimal sphereOS,
    BigDecimal cylinderOS,
    Integer axisOS,
    BigDecimal addition,
    String treatment,
    String recommendedFrameType) {
}
