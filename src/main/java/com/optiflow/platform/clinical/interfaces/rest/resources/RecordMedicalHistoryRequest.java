package com.optiflow.platform.clinical.interfaces.rest.resources;

import jakarta.validation.constraints.Size;
import java.util.List;

public record RecordMedicalHistoryRequest(
    @Size(max = 20) List<@Size(max = 100) String> allergies,
    @Size(max = 20) List<@Size(max = 100) String> previousConditions,
    @Size(max = 1000) String familyOcularHistory) {
}
