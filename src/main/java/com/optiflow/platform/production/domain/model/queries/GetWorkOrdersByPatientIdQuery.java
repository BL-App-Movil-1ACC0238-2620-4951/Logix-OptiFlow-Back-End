package com.optiflow.platform.production.domain.model.queries;

import com.optiflow.platform.production.domain.model.valueobjects.PatientId;

public record GetWorkOrdersByPatientIdQuery(PatientId patientId) {
}
