package com.optiflow.platform.clinical.application.commands;

import com.optiflow.platform.clinical.domain.valueobjects.AppointmentId;
import com.optiflow.platform.clinical.domain.valueobjects.PatientId;

public record ExaminePatientCommand(AppointmentId appointmentId, PatientId patientId) {
}
