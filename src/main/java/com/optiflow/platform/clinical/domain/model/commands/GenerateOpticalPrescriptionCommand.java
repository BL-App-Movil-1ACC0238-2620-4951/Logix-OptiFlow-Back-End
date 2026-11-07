package com.optiflow.platform.clinical.domain.model.commands;

import com.optiflow.platform.clinical.domain.model.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.model.valueobjects.OpticalPrescription;

public record GenerateOpticalPrescriptionCommand(
    ClinicalRecordId clinicalRecordId,
    OpticalPrescription prescription) {
}
