package com.optiflow.booking.domain.event;

import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.domain.vo.PatientId;
import java.time.Instant;

public record FavoriteOpticalStoreSaved(
    PatientId patientId, OpticalStoreId opticalStoreId, Instant occurredAt)
    implements DomainEvent {
}
