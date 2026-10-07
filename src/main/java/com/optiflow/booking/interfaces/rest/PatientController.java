package com.optiflow.booking.interfaces.rest;

import com.optiflow.booking.application.query.GetPatientAppointmentsQuery;
import com.optiflow.booking.application.service.AppointmentApplicationService;
import com.optiflow.booking.application.service.PatientApplicationService;
import com.optiflow.booking.domain.vo.PatientId;
import com.optiflow.booking.interfaces.rest.assembler.FromLoginRequestAssembler;
import com.optiflow.booking.interfaces.rest.assembler.FromRegisterPatientRequestAssembler;
import com.optiflow.booking.interfaces.rest.assembler.SearchBookingResponseAssembler;
import com.optiflow.booking.interfaces.rest.dto.AppointmentResponse;
import com.optiflow.booking.interfaces.rest.dto.LoginRequest;
import com.optiflow.booking.interfaces.rest.dto.LoginResponse;
import com.optiflow.booking.interfaces.rest.dto.PatientResponse;
import com.optiflow.booking.interfaces.rest.dto.RegisterPatientRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PatientController {

  private final PatientApplicationService patientApplicationService;
  private final AppointmentApplicationService appointmentApplicationService;
  private final FromRegisterPatientRequestAssembler registerAssembler;
  private final FromLoginRequestAssembler loginAssembler;
  private final SearchBookingResponseAssembler responseAssembler;

  public PatientController(
      PatientApplicationService patientApplicationService,
      AppointmentApplicationService appointmentApplicationService,
      FromRegisterPatientRequestAssembler registerAssembler,
      FromLoginRequestAssembler loginAssembler,
      SearchBookingResponseAssembler responseAssembler) {
    this.patientApplicationService = patientApplicationService;
    this.appointmentApplicationService = appointmentApplicationService;
    this.registerAssembler = registerAssembler;
    this.loginAssembler = loginAssembler;
    this.responseAssembler = responseAssembler;
  }

  @PostMapping("/patients")
  @ResponseStatus(HttpStatus.CREATED)
  public PatientResponse register(@Valid @RequestBody RegisterPatientRequest request) {
    return responseAssembler.toPatientResponse(
        patientApplicationService.register(registerAssembler.toCommand(request)));
  }

  @PostMapping("/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest request) {
    return responseAssembler.toLoginResponse(
        patientApplicationService.login(loginAssembler.toCommand(request)));
  }

  @GetMapping("/patients/{id}/appointments")
  public List<AppointmentResponse> appointments(@PathVariable UUID id) {
    return appointmentApplicationService
        .findByPatient(new GetPatientAppointmentsQuery(PatientId.of(id)))
        .stream()
        .map(responseAssembler::toAppointmentResponse)
        .toList();
  }
}
