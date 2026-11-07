package com.optiflow.platform.clinical.domain.model.queries;

import com.optiflow.platform.clinical.domain.model.valueobjects.PatientId;

public record GetClinicalRecordsByPatientIdQuery(PatientId patientId) {
}
