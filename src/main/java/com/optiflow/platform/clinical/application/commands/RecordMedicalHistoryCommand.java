package com.optiflow.platform.clinical.application.commands;

import com.optiflow.platform.clinical.domain.valueobjects.ClinicalRecordId;
import java.util.List;

public record RecordMedicalHistoryCommand(
    ClinicalRecordId clinicalRecordId,
    List<String> allergies,
    List<String> previousConditions,
    String familyOcularHistory) {
}
