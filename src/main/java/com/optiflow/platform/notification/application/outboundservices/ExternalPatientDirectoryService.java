package com.optiflow.platform.notification.application.outboundservices;

import com.optiflow.platform.notification.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Port to the appointments and patients of Search &amp; Booking. */
public interface ExternalPatientDirectoryService {

  Optional<ScheduledAppointment> findAppointment(AppointmentId appointmentId);

  List<PatientId> findPatientsWithBirthday(LocalDate date);
}
