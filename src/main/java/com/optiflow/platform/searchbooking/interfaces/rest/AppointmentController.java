package com.optiflow.platform.searchbooking.interfaces.rest;

import com.optiflow.platform.searchbooking.application.commands.CancelAppointmentCommand;
import com.optiflow.platform.searchbooking.application.commands.CompleteAppointmentCommand;
import com.optiflow.platform.searchbooking.application.queries.GetAppointmentQuery;
import com.optiflow.platform.searchbooking.application.queries.GetAppointmentsByOpticalStoreQuery;
import com.optiflow.platform.searchbooking.application.services.AppointmentApplicationService;
import com.optiflow.platform.searchbooking.domain.valueobjects.AppointmentId;
import com.optiflow.platform.searchbooking.domain.valueobjects.AppointmentStatus;
import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.FromBookAppointmentRequestAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.SearchBookingResponseAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.AppointmentResponse;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.BookAppointmentRequest;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.UpdateAppointmentStatusRequest;
import com.optiflow.platform.shared.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.exceptions.DomainException;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import com.optiflow.platform.shared.interfaces.rest.CreatedResourceResponses;
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
  private final SearchBookingResponseAssembler responseAssembler;

  public AppointmentController(
      AppointmentApplicationService appointmentApplicationService,
      FromBookAppointmentRequestAssembler bookAssembler,
      SearchBookingResponseAssembler responseAssembler) {
    this.appointmentApplicationService = appointmentApplicationService;
    this.bookAssembler = bookAssembler;
    this.responseAssembler = responseAssembler;
  }

  @PostMapping("/appointments")
  public ResponseEntity<AppointmentResponse> book(@Valid @RequestBody BookAppointmentRequest request) {
    AppointmentResponse response = responseAssembler.toAppointmentResponse(
        appointmentApplicationService.book(bookAssembler.toCommand(request)));
    return CreatedResourceResponses.at("/appointments/{id}", response, response.id());
  }

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

  @GetMapping("/appointments/{id}")
  public AppointmentResponse get(@PathVariable UUID id) {
    return responseAssembler.toAppointmentResponse(
        appointmentApplicationService.getById(new GetAppointmentQuery(AppointmentId.of(id))));
  }

  @PatchMapping("/appointments/{id}/status")
  public AppointmentResponse updateStatus(
      @PathVariable UUID id, @Valid @RequestBody UpdateAppointmentStatusRequest request) {
    AppointmentId appointmentId = AppointmentId.of(id);
    AppointmentStatus status = parseAppointmentStatus(request.status());
    return responseAssembler.toAppointmentResponse(switch (status) {
      case CANCELLED -> appointmentApplicationService.cancel(new CancelAppointmentCommand(appointmentId));
      case COMPLETED -> appointmentApplicationService.complete(new CompleteAppointmentCommand(appointmentId));
      case PENDING, CONFIRMED ->
          throw new DomainException("Appointment status cannot be changed to " + status + ".", 400);
    });
  }

  private static AppointmentStatus parseAppointmentStatus(String status) {
    try {
      return AppointmentStatus.valueOf(status.trim().toUpperCase());
    } catch (IllegalArgumentException exception) {
      throw new DomainException("Appointment status must be CANCELLED or COMPLETED.", 400);
    }
  }
}
