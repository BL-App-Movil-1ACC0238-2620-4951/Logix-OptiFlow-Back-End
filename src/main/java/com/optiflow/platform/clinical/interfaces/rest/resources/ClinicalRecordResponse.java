package com.optiflow.platform.clinical.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record ClinicalRecordResponse(
    UUID id,
    UUID patientId,
    UUID appointmentId,
    Instant examinationDate,
    String observations,
    String status,
    MedicalHistoryResponse medicalHistory,
    OpticalPrescriptionResponse prescription) {
}
