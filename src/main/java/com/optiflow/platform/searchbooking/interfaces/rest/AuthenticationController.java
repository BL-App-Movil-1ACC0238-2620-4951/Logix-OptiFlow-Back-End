package com.optiflow.platform.searchbooking.interfaces.rest;

import com.optiflow.platform.searchbooking.application.services.PatientApplicationService;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.FromLoginRequestAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.SearchBookingResponseAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.AuthTokenResponse;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.LoginRequest;
import com.optiflow.platform.shared.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import com.optiflow.platform.shared.interfaces.rest.CreatedResourceResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = OpenApiTags.AUTHENTICATION)
@RestController
@RequestMapping(ApiV1.BASE_PATH)
public class AuthenticationController {

  private final PatientApplicationService patientApplicationService;
  private final FromLoginRequestAssembler loginAssembler;
  private final SearchBookingResponseAssembler responseAssembler;

  public AuthenticationController(
      PatientApplicationService patientApplicationService,
      FromLoginRequestAssembler loginAssembler,
      SearchBookingResponseAssembler responseAssembler) {
    this.patientApplicationService = patientApplicationService;
    this.loginAssembler = loginAssembler;
    this.responseAssembler = responseAssembler;
  }

  @PostMapping("/auth/tokens")
  public ResponseEntity<AuthTokenResponse> create(@Valid @RequestBody LoginRequest request) {
    AuthTokenResponse response = responseAssembler.toAuthTokenResponse(
        patientApplicationService.login(loginAssembler.toCommand(request)));
    return CreatedResourceResponses.at("/auth/tokens", response);
  }
}
