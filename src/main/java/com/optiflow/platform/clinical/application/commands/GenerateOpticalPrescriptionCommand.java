package com.optiflow.platform.clinical.application.commands;

import com.optiflow.platform.clinical.domain.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.valueobjects.OpticalPrescription;

public record GenerateOpticalPrescriptionCommand(
    ClinicalRecordId clinicalRecordId,
    OpticalPrescription prescription) {
}
