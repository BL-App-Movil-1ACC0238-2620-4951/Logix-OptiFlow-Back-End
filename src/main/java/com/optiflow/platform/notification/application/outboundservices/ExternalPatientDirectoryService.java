package com.optiflow.platform.notification.application.outboundservices;

import com.optiflow.platform.notification.domain.model.valueobjects.AppointmentId;
import java.util.Optional;

/** Port to the appointments and patients of Search &amp; Booking. */
public interface ExternalPatientDirectoryService {

  Optional<ScheduledAppointment> findAppointment(AppointmentId appointmentId);
}
