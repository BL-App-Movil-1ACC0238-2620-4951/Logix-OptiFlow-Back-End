package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.commands.BookAppointmentCommand;
import com.optiflow.platform.searchbooking.application.queries.SearchOpticalStoresQuery;
import com.optiflow.platform.searchbooking.domain.entities.OpticalStore;
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
