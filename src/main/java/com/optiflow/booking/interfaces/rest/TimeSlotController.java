package com.optiflow.booking.interfaces.rest;

import com.optiflow.booking.application.query.GetAvailableTimeSlotsQuery;
import com.optiflow.booking.application.service.OpticalStoreApplicationService;
import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.interfaces.rest.assembler.SearchBookingResponseAssembler;
import com.optiflow.booking.interfaces.rest.dto.TimeSlotListResponse;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TimeSlotController {

  private final OpticalStoreApplicationService opticalStoreApplicationService;
  private final SearchBookingResponseAssembler responseAssembler;

  public TimeSlotController(
      OpticalStoreApplicationService opticalStoreApplicationService,
      SearchBookingResponseAssembler responseAssembler) {
    this.opticalStoreApplicationService = opticalStoreApplicationService;
    this.responseAssembler = responseAssembler;
  }

  @GetMapping("/optical-stores/{id}/availability")
  public TimeSlotListResponse availability(@PathVariable UUID id) {
    return responseAssembler.toTimeSlotList(
        opticalStoreApplicationService.availability(new GetAvailableTimeSlotsQuery(OpticalStoreId.of(id))));
  }
}
