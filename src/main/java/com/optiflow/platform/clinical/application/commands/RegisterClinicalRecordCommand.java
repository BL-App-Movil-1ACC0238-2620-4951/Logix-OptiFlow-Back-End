package com.optiflow.platform.clinical.application.commands;

import com.optiflow.platform.clinical.domain.valueobjects.AppointmentId;
import com.optiflow.platform.clinical.domain.valueobjects.PatientId;
import java.time.Instant;

public record RegisterClinicalRecordCommand(
    PatientId patientId,
    AppointmentId appointmentId,
    Instant examinationDate,
    String observations) {
}
