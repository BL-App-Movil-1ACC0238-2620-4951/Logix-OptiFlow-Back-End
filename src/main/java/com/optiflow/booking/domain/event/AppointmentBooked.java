package com.optiflow.booking.domain.event;

import com.optiflow.booking.domain.vo.AppointmentId;
import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.domain.vo.PatientId;
import com.optiflow.booking.domain.vo.TimeSlotId;
import java.time.Instant;

public record AppointmentBooked(
    AppointmentId appointmentId,
    PatientId patientId,
    OpticalStoreId opticalStoreId,
    TimeSlotId timeSlotId,
    Instant occurredAt)
    implements DomainEvent {
}
