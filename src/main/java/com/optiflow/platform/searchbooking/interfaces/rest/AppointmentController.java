package com.optiflow.platform.searchbooking.interfaces.rest;

import com.optiflow.platform.searchbooking.application.services.AppointmentApplicationService;
import com.optiflow.platform.searchbooking.domain.model.commands.CancelAppointmentCommand;
import com.optiflow.platform.searchbooking.domain.model.commands.CompleteAppointmentCommand;
import com.optiflow.platform.searchbooking.domain.model.queries.GetAppointmentQuery;
import com.optiflow.platform.searchbooking.domain.model.queries.GetAppointmentsByOpticalStoreQuery;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.FromBookAppointmentRequestAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.FromUpdateAppointmentStatusRequestAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.SearchBookingResponseAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.AppointmentResponse;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.BookAppointmentRequest;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.UpdateAppointmentStatusRequest;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import com.optiflow.platform.shared.interfaces.rest.CreatedResourceResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = OpenApiTags.APPOINTMENTS)
@RestController
@RequestMapping(ApiV1.BASE_PATH)
public class AppointmentController {

  private final AppointmentApplicationService appointmentApplicationService;
  private final FromBookAppointmentRequestAssembler bookAssembler;
  private final FromUpdateAppointmentStatusRequestAssembler statusAssembler;
  private final SearchBookingResponseAssembler responseAssembler;

  public AppointmentController(
      AppointmentApplicationService appointmentApplicationService,
      FromBookAppointmentRequestAssembler bookAssembler,
      FromUpdateAppointmentStatusRequestAssembler statusAssembler,
      SearchBookingResponseAssembler responseAssembler) {
    this.appointmentApplicationService = appointmentApplicationService;
    this.bookAssembler = bookAssembler;
    this.statusAssembler = statusAssembler;
    this.responseAssembler = responseAssembler;
  }

  @Operation(summary = "Book an appointment",
      description = "Books an available time slot for a patient; the appointment starts CONFIRMED. "
          + "It publishes AppointmentBooked, which opens the clinical record and schedules "
          + "the reminders. Patients and the clinical staff can book.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Appointment booked."),
      @ApiResponse(responseCode = "400",
          description = "The time slot does not belong to the optical store."),
      @ApiResponse(responseCode = "404",
          description = "The patient, the optical store or the time slot was not found."),
      @ApiResponse(responseCode = "409",
          description = "The time slot is no longer available or the store is not available.")
  })
  @PostMapping("/appointments")
  public ResponseEntity<AppointmentResponse> book(@Valid @RequestBody BookAppointmentRequest request) {
    AppointmentResponse response = responseAssembler.toAppointmentResponse(
        appointmentApplicationService.book(bookAssembler.toCommand(request)));
    return CreatedResourceResponses.at("/appointments/{id}", response, response.id());
  }

  @Operation(summary = "Get the agenda of a store",
      description = "Returns the appointments of the optical store on the given date. Only "
          + "optometrists and admins.")
  @ApiResponses(@ApiResponse(responseCode = "200", description = "Appointments of the day."))
  @GetMapping("/appointments")
  public List<AppointmentResponse> listByOpticalStore(
      @RequestParam UUID opticalStoreId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    return appointmentApplicationService
        .findByOpticalStoreAndDate(
            new GetAppointmentsByOpticalStoreQuery(OpticalStoreId.of(opticalStoreId), date))
        .stream()
        .map(responseAssembler::toAppointmentResponse)
        .toList();
  }

  @Operation(summary = "Get an appointment",
      description = "Returns the appointment with its patient, store and time slot.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Appointment found."),
      @ApiResponse(responseCode = "404", description = "The appointment was not found.")
  })
  @GetMapping("/appointments/{id}")
  public AppointmentResponse get(@PathVariable UUID id) {
    return responseAssembler.toAppointmentResponse(
        appointmentApplicationService.getById(new GetAppointmentQuery(AppointmentId.of(id))));
  }

  @Operation(summary = "Cancel or complete an appointment",
      description = "With status CANCELLED, cancels the appointment and frees its time slot; with "
          + "COMPLETED, marks it as attended. To reschedule, book another time slot and "
          + "cancel this appointment.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Appointment updated."),
      @ApiResponse(responseCode = "400", description = "The status is not CANCELLED or COMPLETED."),
      @ApiResponse(responseCode = "404", description = "The appointment was not found."),
      @ApiResponse(responseCode = "409",
          description = "The appointment can no longer be cancelled or completed.")
  })
  @PatchMapping("/appointments/{id}/status")
  public AppointmentResponse updateStatus(
      @PathVariable UUID id, @Valid @RequestBody UpdateAppointmentStatusRequest request) {
    return responseAssembler.toAppointmentResponse(
        switch (statusAssembler.toCommand(id, request)) {
          case CancelAppointmentCommand command -> appointmentApplicationService.cancel(command);
          case CompleteAppointmentCommand command -> appointmentApplicationService.complete(command);
        });
  }
}
