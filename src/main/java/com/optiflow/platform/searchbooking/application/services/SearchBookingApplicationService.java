package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.results.AppointmentDetails;
import com.optiflow.platform.searchbooking.domain.model.commands.BookAppointmentCommand;
import com.optiflow.platform.searchbooking.domain.model.entities.OpticalStore;
import com.optiflow.platform.searchbooking.domain.model.queries.FilterOpticalStoresQuery;
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

  public List<OpticalStore> searchStores(FilterOpticalStoresQuery query) {
    return opticalStoreApplicationService.findStores(query);
  }

  public AppointmentDetails book(BookAppointmentCommand command) {
    return appointmentApplicationService.book(command);
  }
}
