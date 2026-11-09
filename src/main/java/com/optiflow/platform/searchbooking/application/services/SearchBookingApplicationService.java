package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.results.AppointmentDetails;
import com.optiflow.platform.searchbooking.domain.model.queries.GetAppointmentQuery;
import org.springframework.stereotype.Service;

/** Entry point of Search &amp; Booking for the other bounded contexts. */
@Service
public class SearchBookingApplicationService {

  private final AppointmentApplicationService appointmentApplicationService;

  public SearchBookingApplicationService(
      AppointmentApplicationService appointmentApplicationService) {
    this.appointmentApplicationService = appointmentApplicationService;
  }

  public AppointmentDetails getAppointment(GetAppointmentQuery query) {
    return appointmentApplicationService.getById(query);
  }
}
