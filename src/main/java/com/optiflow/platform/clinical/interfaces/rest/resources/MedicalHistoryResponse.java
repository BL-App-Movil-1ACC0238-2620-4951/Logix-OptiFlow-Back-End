package com.optiflow.platform.clinical.interfaces.rest.resources;

import java.time.Instant;
import java.util.List;

public record MedicalHistoryResponse(
    List<String> allergies,
    List<String> previousConditions,
    String familyOcularHistory,
    Instant lastUpdated) {
}
