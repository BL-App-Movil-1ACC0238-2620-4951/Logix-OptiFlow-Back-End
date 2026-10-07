package com.optiflow.platform.searchbooking.application.queries;

import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;

public record GetPatientAppointmentsQuery(PatientId patientId) {
}
