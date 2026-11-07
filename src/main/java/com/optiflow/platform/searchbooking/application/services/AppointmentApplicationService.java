package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.commands.BookAppointmentCommand;
import com.optiflow.platform.searchbooking.application.commands.CancelAppointmentCommand;
import com.optiflow.platform.searchbooking.application.commands.CompleteAppointmentCommand;
import com.optiflow.platform.searchbooking.application.queries.GetAppointmentQuery;
import com.optiflow.platform.searchbooking.application.queries.GetAppointmentsByOpticalStoreQuery;
import com.optiflow.platform.searchbooking.application.queries.GetPatientAppointmentsQuery;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AppointmentApplicationService {

  private final BookAppointmentCommandHandler bookAppointmentCommandHandler;
  private final CancelAppointmentCommandHandler cancelAppointmentCommandHandler;
  private final CompleteAppointmentCommandHandler completeAppointmentCommandHandler;
  private final GetAppointmentQueryService getAppointmentQueryService;
  private final GetPatientAppointmentsQueryService getPatientAppointmentsQueryService;
  private final GetAppointmentsByOpticalStoreQueryService getAppointmentsByOpticalStoreQueryService;

  public AppointmentApplicationService(
      BookAppointmentCommandHandler bookAppointmentCommandHandler,
      CancelAppointmentCommandHandler cancelAppointmentCommandHandler,
      CompleteAppointmentCommandHandler completeAppointmentCommandHandler,
      GetAppointmentQueryService getAppointmentQueryService,
      GetPatientAppointmentsQueryService getPatientAppointmentsQueryService,
      GetAppointmentsByOpticalStoreQueryService getAppointmentsByOpticalStoreQueryService) {
    this.bookAppointmentCommandHandler = bookAppointmentCommandHandler;
    this.cancelAppointmentCommandHandler = cancelAppointmentCommandHandler;
    this.completeAppointmentCommandHandler = completeAppointmentCommandHandler;
    this.getAppointmentQueryService = getAppointmentQueryService;
    this.getPatientAppointmentsQueryService = getPatientAppointmentsQueryService;
    this.getAppointmentsByOpticalStoreQueryService = getAppointmentsByOpticalStoreQueryService;
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

  public AppointmentDetails cancel(CancelAppointmentCommand command) {
    return cancelAppointmentCommandHandler.handle(command);
  }

  public AppointmentDetails complete(CompleteAppointmentCommand command) {
    return completeAppointmentCommandHandler.handle(command);
  }

  public List<AppointmentDetails> findByOpticalStoreAndDate(GetAppointmentsByOpticalStoreQuery query) {
    return getAppointmentsByOpticalStoreQueryService.handle(query);
  }
}
