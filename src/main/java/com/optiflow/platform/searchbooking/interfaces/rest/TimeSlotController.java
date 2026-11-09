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
import com.optiflow.platform.shared.interfaces.rest.CreatedResourceResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

  @Operation(summary = "List the available time slots",
      description = "Returns the time slots of the store that can still be booked. Public route.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Available time slots."),
      @ApiResponse(responseCode = "404", description = "The optical store was not found.")
  })
  @SecurityRequirements
  @GetMapping("/optical-stores/{id}/time-slots")
  public List<TimeSlotResponse> list(@PathVariable UUID id) {
    return responseAssembler.toTimeSlotResponses(
        opticalStoreApplicationService.availability(
            new GetAvailableTimeSlotsQuery(OpticalStoreId.of(id))));
  }

  @Operation(summary = "Publish time slots",
      description = "The admin publishes new time slots of the store; they are created available. "
          + "The Location header points to the list of time slots.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Time slots published."),
      @ApiResponse(responseCode = "404", description = "The optical store was not found.")
  })
  @PostMapping("/optical-stores/{id}/time-slots")
  public ResponseEntity<List<TimeSlotResponse>> publish(
      @PathVariable UUID id, @Valid @RequestBody PublishTimeSlotsRequest request) {
    List<TimeSlotResponse> response = responseAssembler.toTimeSlotResponses(
        opticalStoreApplicationService.publishTimeSlots(
            publishAssembler.toCommand(id, request)));
    return CreatedResourceResponses.at("/optical-stores/{id}/time-slots", response, id);
  }
}
