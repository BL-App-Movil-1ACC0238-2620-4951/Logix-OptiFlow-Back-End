package com.optiflow.booking.application.service;

import com.optiflow.booking.application.command.BookAppointmentCommand;
import com.optiflow.booking.application.handler.BookAppointmentCommandHandler;
import com.optiflow.booking.application.handler.GetAppointmentQueryService;
import com.optiflow.booking.application.handler.GetPatientAppointmentsQueryService;
import com.optiflow.booking.application.query.GetAppointmentQuery;
import com.optiflow.booking.application.query.GetPatientAppointmentsQuery;
import com.optiflow.booking.application.result.AppointmentDetails;
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
