package com.optiflow.platform.production.application.queries;

import com.optiflow.platform.production.domain.valueobjects.PatientId;

public record GetWorkOrdersByPatientIdQuery(PatientId patientId) {
}
