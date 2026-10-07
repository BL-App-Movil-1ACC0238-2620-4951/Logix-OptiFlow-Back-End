package com.optiflow.platform.searchbooking.domain.events;

import com.optiflow.platform.searchbooking.domain.valueobjects.AppointmentId;
import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.domain.valueobjects.TimeSlotId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record AppointmentBooked(
    AppointmentId appointmentId,
    PatientId patientId,
    OpticalStoreId opticalStoreId,
    TimeSlotId timeSlotId,
    Instant occurredAt)
    implements DomainEvent {
}
