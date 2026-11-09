package com.optiflow.platform.clinical.domain.model.commands;

import com.optiflow.platform.clinical.domain.model.valueobjects.ClinicalRecordId;
import java.util.List;

public record RecordMedicalHistoryCommand(
    ClinicalRecordId clinicalRecordId,
    List<String> allergies,
    List<String> previousConditions,
    String familyOcularHistory) {
}
