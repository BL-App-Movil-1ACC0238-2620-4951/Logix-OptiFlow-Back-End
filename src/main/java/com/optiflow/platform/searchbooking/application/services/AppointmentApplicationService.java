package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.commands.BookAppointmentCommand;
import com.optiflow.platform.searchbooking.application.queries.GetAppointmentQuery;
import com.optiflow.platform.searchbooking.application.queries.GetPatientAppointmentsQuery;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AppointmentApplicationService {

  private final BookAppointmentCommandHandler bookAppointmentCommandHandler;
  private final GetAppointmentQueryService getAppointmentQueryService;
  private final GetPatientAppointmentsQueryService getPatientAppointmentsQueryService;

  public AppointmentApplicationService(
      BookAppointmentCommandHandler bookAppointmentCommandHandler,
      GetAppointmentQueryService getAppointmentQueryService,
      GetPatientAppointmentsQueryService getPatientAppointmentsQueryService) {
    this.bookAppointmentCommandHandler = bookAppointmentCommandHandler;
    this.getAppointmentQueryService = getAppointmentQueryService;
    this.getPatientAppointmentsQueryService = getPatientAppointmentsQueryService;
  }

  public AppointmentDetails book(BookAppointmentCommand command) {
    return bookAppointmentCommandHandler.handle(command);
  }

  public AppointmentDetails getById(GetAppointmentQuery query) {
    return getAppointmentQueryService.handle(query);
  }

  public List<AppointmentDetails> findByPatient(GetPatientAppointmentsQuery query) {
    return getPatientAppointmentsQueryService.handle(query);
  }
}
