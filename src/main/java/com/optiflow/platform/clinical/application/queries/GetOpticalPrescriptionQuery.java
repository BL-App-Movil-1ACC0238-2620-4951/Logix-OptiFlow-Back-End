package com.optiflow.platform.clinical.application.queries;

import com.optiflow.platform.clinical.domain.valueobjects.ClinicalRecordId;

public record GetOpticalPrescriptionQuery(ClinicalRecordId clinicalRecordId) {
}
