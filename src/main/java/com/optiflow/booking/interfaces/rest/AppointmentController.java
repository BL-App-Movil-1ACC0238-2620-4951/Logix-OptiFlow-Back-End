package com.optiflow.booking.interfaces.rest;

import com.optiflow.booking.application.query.GetAppointmentQuery;
import com.optiflow.booking.application.service.AppointmentApplicationService;
import com.optiflow.booking.domain.vo.AppointmentId;
import com.optiflow.booking.interfaces.rest.assembler.FromBookAppointmentRequestAssembler;
import com.optiflow.booking.interfaces.rest.assembler.SearchBookingResponseAssembler;
import com.optiflow.booking.interfaces.rest.dto.AppointmentResponse;
import com.optiflow.booking.interfaces.rest.dto.BookAppointmentRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
  @ResponseStatus(HttpStatus.CREATED)
  public AppointmentResponse book(@Valid @RequestBody BookAppointmentRequest request) {
    return responseAssembler.toAppointmentResponse(
        appointmentApplicationService.book(bookAssembler.toCommand(request)));
  }

  @GetMapping("/appointments/{id}")
  public AppointmentResponse get(@PathVariable UUID id) {
    return responseAssembler.toAppointmentResponse(
        appointmentApplicationService.getById(new GetAppointmentQuery(AppointmentId.of(id))));
  }
}
