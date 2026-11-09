package com.optiflow.platform.searchbooking.interfaces.rest;

import com.optiflow.platform.searchbooking.application.services.AppointmentApplicationService;
import com.optiflow.platform.searchbooking.application.services.PatientApplicationService;
import com.optiflow.platform.searchbooking.domain.model.queries.GetPatientAppointmentsQuery;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.FromRegisterPatientRequestAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.SearchBookingResponseAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.AppointmentResponse;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.PatientResponse;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.RegisterPatientRequest;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.infrastructure.security.AuthenticatedUser;
import com.optiflow.platform.shared.infrastructure.security.PatientDataAccessGuard;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import com.optiflow.platform.shared.interfaces.rest.CreatedResourceResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = OpenApiTags.PATIENTS)
@RestController
@RequestMapping(ApiV1.BASE_PATH)
public class PatientController {

  private final PatientApplicationService patientApplicationService;
  private final AppointmentApplicationService appointmentApplicationService;
  private final FromRegisterPatientRequestAssembler registerAssembler;
  private final SearchBookingResponseAssembler responseAssembler;
  private final PatientDataAccessGuard patientDataAccessGuard;

  public PatientController(
      PatientApplicationService patientApplicationService,
      AppointmentApplicationService appointmentApplicationService,
      FromRegisterPatientRequestAssembler registerAssembler,
      SearchBookingResponseAssembler responseAssembler,
      PatientDataAccessGuard patientDataAccessGuard) {
    this.patientApplicationService = patientApplicationService;
    this.appointmentApplicationService = appointmentApplicationService;
    this.registerAssembler = registerAssembler;
    this.responseAssembler = responseAssembler;
    this.patientDataAccessGuard = patientDataAccessGuard;
  }

  @PostMapping("/patients")
  public ResponseEntity<PatientResponse> register(@Valid @RequestBody RegisterPatientRequest request) {
    PatientResponse response = responseAssembler.toPatientResponse(
        patientApplicationService.register(registerAssembler.toCommand(request)));
    return CreatedResourceResponses.at("/patients/{id}", response, response.id());
  }

  @GetMapping("/patients/{patientId}/appointments")
  public List<AppointmentResponse> appointments(
      @PathVariable UUID patientId, @AuthenticationPrincipal AuthenticatedUser user) {
    patientDataAccessGuard.ensureSelfPatientAccess(user, patientId);
    return appointmentApplicationService
        .findByPatient(new GetPatientAppointmentsQuery(PatientId.of(patientId)))
        .stream()
        .map(responseAssembler::toAppointmentResponse)
        .toList();
  }
}
