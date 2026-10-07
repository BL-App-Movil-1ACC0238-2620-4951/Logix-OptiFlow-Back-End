package com.optiflow.booking.application.service;

import com.optiflow.booking.application.command.BookAppointmentCommand;
import com.optiflow.booking.application.query.SearchOpticalStoresQuery;
import com.optiflow.booking.application.result.AppointmentDetails;
import com.optiflow.booking.domain.model.OpticalStore;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SearchBookingApplicationService {

  private final OpticalStoreApplicationService opticalStoreApplicationService;
  private final AppointmentApplicationService appointmentApplicationService;

  public SearchBookingApplicationService(
      OpticalStoreApplicationService opticalStoreApplicationService,
      AppointmentApplicationService appointmentApplicationService) {
    this.opticalStoreApplicationService = opticalStoreApplicationService;
    this.appointmentApplicationService = appointmentApplicationService;
  }

  public List<OpticalStore> searchStores(SearchOpticalStoresQuery query) {
    return opticalStoreApplicationService.search(query);
  }

  public AppointmentDetails book(BookAppointmentCommand command) {
    return appointmentApplicationService.book(command);
  }
}
