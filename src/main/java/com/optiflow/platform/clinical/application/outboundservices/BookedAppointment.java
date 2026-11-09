package com.optiflow.platform.clinical.application.outboundservices;

import com.optiflow.platform.clinical.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.clinical.domain.model.valueobjects.PatientId;
import java.util.UUID;

/** Clinical view of an appointment owned by the Search & Booking context. */
public record BookedAppointment(
    AppointmentId appointmentId, PatientId patientId, UUID opticalStoreId, boolean active) {
}
