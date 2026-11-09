package com.optiflow.platform.searchbooking.domain.model.events;

import com.optiflow.platform.searchbooking.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.TimeSlotId;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record AppointmentCompleted(
    AppointmentId appointmentId,
    PatientId patientId,
    OpticalStoreId opticalStoreId,
    TimeSlotId timeSlotId,
    Instant occurredAt)
    implements DomainEvent {}
