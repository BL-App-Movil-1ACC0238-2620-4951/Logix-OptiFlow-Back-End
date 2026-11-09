package com.optiflow.platform.clinical.infrastructure.outboundservices;

import com.optiflow.platform.clinical.application.outboundservices.BookedAppointment;
import com.optiflow.platform.clinical.application.outboundservices.ExternalAppointmentService;
import com.optiflow.platform.clinical.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.clinical.domain.model.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.application.services.SearchBookingApplicationService;
import com.optiflow.platform.searchbooking.domain.model.aggregates.Appointment;
import com.optiflow.platform.searchbooking.domain.model.queries.GetAppointmentQuery;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.AppointmentStatus;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Anti-corruption layer that reads appointments from Search & Booking and translates them into
 * the Clinical & Commercial model.
 */
@Component
public class SearchBookingAppointmentAdapter implements ExternalAppointmentService {

  private final SearchBookingApplicationService searchBookingApplicationService;

  public SearchBookingAppointmentAdapter(
      SearchBookingApplicationService searchBookingApplicationService) {
    this.searchBookingApplicationService = searchBookingApplicationService;
  }

  @Override
  public Optional<BookedAppointment> findById(AppointmentId appointmentId) {
    try {
      Appointment appointment = searchBookingApplicationService.getAppointment(new GetAppointmentQuery(
          com.optiflow.platform.searchbooking.domain.model.valueobjects.AppointmentId.of(
              appointmentId.value()))).appointment();
      return Optional.of(new BookedAppointment(
          appointmentId,
          PatientId.of(appointment.patientId().value()),
          appointment.opticalStoreId().value(),
          appointment.status() != AppointmentStatus.CANCELLED));
    } catch (DomainException exception) {
      if (exception.status() == 404) {
        return Optional.empty();
      }
      throw exception;
    }
  }
}
