package com.optiflow.platform.searchbooking.interfaces.rest;

import com.optiflow.platform.searchbooking.application.queries.GetPatientAppointmentsQuery;
import com.optiflow.platform.searchbooking.application.services.AppointmentApplicationService;
import com.optiflow.platform.searchbooking.application.services.PatientApplicationService;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.FromLoginRequestAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.FromRegisterPatientRequestAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.SearchBookingResponseAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.AppointmentResponse;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.LoginRequest;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.LoginResponse;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.PatientResponse;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.RegisterPatientRequest;
import com.optiflow.platform.shared.documentation.openapi.configuration.OpenApiTags;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = OpenApiTags.PATIENTS)
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
