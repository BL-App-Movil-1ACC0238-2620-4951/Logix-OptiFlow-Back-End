package com.optiflow.platform.notification.infrastructure.outboundservices;

import com.optiflow.platform.notification.application.outboundservices.ExternalPatientDirectoryService;
import com.optiflow.platform.notification.application.outboundservices.ScheduledAppointment;
import com.optiflow.platform.notification.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.application.results.AppointmentDetails;
import com.optiflow.platform.searchbooking.application.services.SearchBookingApplicationService;
import com.optiflow.platform.searchbooking.domain.model.queries.GetAppointmentQuery;
import com.optiflow.platform.searchbooking.domain.model.queries.GetPatientsByBirthdayQuery;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.AppointmentStatus;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Reads appointments and birthdays from Search &amp; Booking without exposing its
 * {@code Patient} and {@code Appointment} models to the Notification &amp; Loyalty context.
 */
@Component
public class PatientDirectoryAdapter implements ExternalPatientDirectoryService {

  private final SearchBookingApplicationService searchBookingApplicationService;

  public PatientDirectoryAdapter(SearchBookingApplicationService searchBookingApplicationService) {
    this.searchBookingApplicationService = searchBookingApplicationService;
  }

  @Override
  public Optional<ScheduledAppointment> findAppointment(AppointmentId appointmentId) {
    try {
      AppointmentDetails details = searchBookingApplicationService.getAppointment(
          new GetAppointmentQuery(
              com.optiflow.platform.searchbooking.domain.model.valueobjects.AppointmentId.of(
                  appointmentId.value())));
      return Optional.of(new ScheduledAppointment(
          appointmentId,
          PatientId.of(details.appointment().patientId().value()),
          details.timeSlot().startDateTime(),
          details.appointment().status() == AppointmentStatus.CANCELLED));
    } catch (DomainException exception) {
      if (exception.status() == 404) {
        return Optional.empty();
      }
      throw exception;
    }
  }

  @Override
  public List<PatientId> findPatientsWithBirthday(LocalDate date) {
    return searchBookingApplicationService
        .getPatientsByBirthday(new GetPatientsByBirthdayQuery(date)).stream()
        .map(patientId -> PatientId.of(patientId.value()))
        .toList();
  }
}
