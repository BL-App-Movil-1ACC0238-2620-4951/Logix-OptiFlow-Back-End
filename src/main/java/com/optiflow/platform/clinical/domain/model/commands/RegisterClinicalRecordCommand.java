package com.optiflow.platform.clinical.domain.model.commands;

import com.optiflow.platform.clinical.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.clinical.domain.model.valueobjects.PatientId;
import java.time.Instant;

public record RegisterClinicalRecordCommand(
    PatientId patientId,
    AppointmentId appointmentId,
    Instant examinationDate,
    String observations) {
}
