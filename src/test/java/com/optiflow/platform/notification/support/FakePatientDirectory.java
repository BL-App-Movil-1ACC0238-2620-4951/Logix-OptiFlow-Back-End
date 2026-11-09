package com.optiflow.platform.notification.support;

import com.optiflow.platform.notification.application.outboundservices.ExternalPatientDirectoryService;
import com.optiflow.platform.notification.application.outboundservices.ScheduledAppointment;
import com.optiflow.platform.notification.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FakePatientDirectory implements ExternalPatientDirectoryService {

  private final Map<AppointmentId, ScheduledAppointment> appointments = new ConcurrentHashMap<>();
  private final Map<PatientId, LocalDate> birthDates = new ConcurrentHashMap<>();

  public void bornOn(UUID patientId, LocalDate birthDate) {
    birthDates.put(PatientId.of(patientId), birthDate);
  }

  public void book(UUID appointmentId, UUID patientId, Instant startsAt) {
    AppointmentId id = AppointmentId.of(appointmentId);
    appointments.put(id, new ScheduledAppointment(id, PatientId.of(patientId), startsAt, false));
  }

  public void cancel(UUID appointmentId) {
    AppointmentId id = AppointmentId.of(appointmentId);
    ScheduledAppointment booked = appointments.get(id);
    appointments.put(id, new ScheduledAppointment(
        id, booked.patientId(), booked.startsAt(), true));
  }

  @Override
  public Optional<ScheduledAppointment> findAppointment(AppointmentId appointmentId) {
    return Optional.ofNullable(appointments.get(appointmentId));
  }

  @Override
  public List<PatientId> findPatientsWithBirthday(LocalDate date) {
    return birthDates.entrySet().stream()
        .filter(entry -> entry.getValue().getMonth() == date.getMonth()
            && entry.getValue().getDayOfMonth() == date.getDayOfMonth())
        .map(Map.Entry::getKey)
        .toList();
  }
}
