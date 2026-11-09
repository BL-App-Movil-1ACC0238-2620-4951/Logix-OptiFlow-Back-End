package com.optiflow.platform.searchbooking.interfaces.rest;

import com.optiflow.platform.searchbooking.application.services.PatientApplicationService;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.FromLoginRequestAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.SearchBookingResponseAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.LoginRequest;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.LoginResponse;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import com.optiflow.platform.shared.interfaces.rest.CreatedResourceResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
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

  @Operation(summary = "Log in",
      description = "Issues a JWT access token for a patient or a staff member with their "
          + "email and password. Send it as Authorization: Bearer <token> in the other requests.")
  @ApiResponses({
      @ApiResponse(responseCode = "201",
          description = "Token issued with its expiration, the user id and the role."),
      @ApiResponse(responseCode = "401", description = "The credentials are invalid.")
  })
  @SecurityRequirements
  @PostMapping("/auth/tokens")
  public ResponseEntity<LoginResponse> create(@Valid @RequestBody LoginRequest request) {
    LoginResponse response = responseAssembler.toLoginResponse(
        patientApplicationService.login(loginAssembler.toCommand(request)));
    return CreatedResourceResponses.at("/auth/tokens", response);
  }
}
