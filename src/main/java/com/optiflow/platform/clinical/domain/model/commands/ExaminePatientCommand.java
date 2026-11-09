package com.optiflow.platform.clinical.domain.model.commands;

import com.optiflow.platform.clinical.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.clinical.domain.model.valueobjects.PatientId;

public record ExaminePatientCommand(AppointmentId appointmentId, PatientId patientId) {
}
