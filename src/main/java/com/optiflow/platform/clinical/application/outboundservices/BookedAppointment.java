package com.optiflow.platform.clinical.application.outboundservices;

import com.optiflow.platform.clinical.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.clinical.domain.model.valueobjects.PatientId;

/** Clinical view of an appointment owned by the Search & Booking context. */
public record BookedAppointment(AppointmentId appointmentId, PatientId patientId, boolean active) {
}
