package com.optiflow.platform.notification.application.outboundservices;

import com.optiflow.platform.notification.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import java.time.Instant;

/** Notification view of an appointment owned by Search &amp; Booking. */
public record ScheduledAppointment(
    AppointmentId appointmentId, PatientId patientId, Instant startsAt, boolean cancelled) {
}
