package com.optiflow.platform.searchbooking.interfaces.rest;

import com.optiflow.platform.searchbooking.application.services.OpticalStoreApplicationService;
import com.optiflow.platform.searchbooking.domain.model.queries.GetAvailableTimeSlotsQuery;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.FromPublishTimeSlotsRequestAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.SearchBookingResponseAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.PublishTimeSlotsRequest;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.TimeSlotResponse;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = OpenApiTags.AVAILABILITY)
@RestController
@RequestMapping(ApiV1.BASE_PATH)
public class TimeSlotController {

  private final OpticalStoreApplicationService opticalStoreApplicationService;
  private final SearchBookingResponseAssembler responseAssembler;
  private final FromPublishTimeSlotsRequestAssembler publishAssembler;

  public TimeSlotController(
      OpticalStoreApplicationService opticalStoreApplicationService,
      SearchBookingResponseAssembler responseAssembler,
      FromPublishTimeSlotsRequestAssembler publishAssembler) {
    this.opticalStoreApplicationService = opticalStoreApplicationService;
    this.responseAssembler = responseAssembler;
    this.publishAssembler = publishAssembler;
  }

  @GetMapping("/optical-stores/{opticalStoreId}/time-slots")
  public List<TimeSlotResponse> list(@PathVariable UUID opticalStoreId) {
    return responseAssembler.toTimeSlotResponses(
        opticalStoreApplicationService.availability(
            new GetAvailableTimeSlotsQuery(OpticalStoreId.of(opticalStoreId))));
  }

  @PostMapping("/optical-stores/{opticalStoreId}/time-slots")
  @ResponseStatus(HttpStatus.CREATED)
  public List<TimeSlotResponse> publish(
      @PathVariable UUID opticalStoreId, @Valid @RequestBody PublishTimeSlotsRequest request) {
    return responseAssembler.toTimeSlotResponses(
        opticalStoreApplicationService.publishTimeSlots(
            publishAssembler.toCommand(opticalStoreId, request)));
  }
}
