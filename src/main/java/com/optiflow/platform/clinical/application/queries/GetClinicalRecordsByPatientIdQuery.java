package com.optiflow.platform.clinical.application.queries;

import com.optiflow.platform.clinical.domain.valueobjects.PatientId;

public record GetClinicalRecordsByPatientIdQuery(PatientId patientId) {
}
