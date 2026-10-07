package com.optiflow.platform.clinical.application.services;

import com.optiflow.platform.clinical.domain.valueobjects.AppointmentId;
import java.util.Optional;

/** Outbound port to look up appointments booked in the Search & Booking context. */
public interface ExternalAppointmentService {

  Optional<BookedAppointment> findById(AppointmentId appointmentId);
}
