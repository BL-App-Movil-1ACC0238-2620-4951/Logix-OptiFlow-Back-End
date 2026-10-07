package com.optiflow.platform.clinical.application.services;

import com.optiflow.platform.clinical.domain.valueobjects.AppointmentId;
import com.optiflow.platform.clinical.domain.valueobjects.PatientId;

/** Clinical view of an appointment owned by the Search & Booking context. */
public record BookedAppointment(AppointmentId appointmentId, PatientId patientId, boolean active) {
}
