package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.results.AppointmentDetails;
import com.optiflow.platform.searchbooking.domain.model.queries.GetAppointmentQuery;
import com.optiflow.platform.searchbooking.domain.model.queries.GetPatientsByBirthdayQuery;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PatientId;
import java.util.List;
import org.springframework.stereotype.Service;

/** Entry point of Search &amp; Booking for the other bounded contexts. */
@Service
public class SearchBookingApplicationService {

  private final AppointmentApplicationService appointmentApplicationService;
  private final PatientApplicationService patientApplicationService;

  public SearchBookingApplicationService(
      AppointmentApplicationService appointmentApplicationService,
      PatientApplicationService patientApplicationService) {
    this.appointmentApplicationService = appointmentApplicationService;
    this.patientApplicationService = patientApplicationService;
  }

  public AppointmentDetails getAppointment(GetAppointmentQuery query) {
    return appointmentApplicationService.getById(query);
  }

  public List<PatientId> getPatientsByBirthday(GetPatientsByBirthdayQuery query) {
    return patientApplicationService.findByBirthday(query);
  }
}
