package com.optiflow.platform.notification.interfaces.rest;

import com.optiflow.platform.notification.application.services.NotificationApplicationService;
import com.optiflow.platform.notification.interfaces.rest.assemblers.FromRegisterDeviceTokenRequestAssembler;
import com.optiflow.platform.notification.interfaces.rest.assemblers.NotificationResourceAssembler;
import com.optiflow.platform.notification.interfaces.rest.resources.DeviceTokenResponse;
import com.optiflow.platform.notification.interfaces.rest.resources.RegisterDeviceTokenRequest;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.infrastructure.security.AuthenticatedUser;
import com.optiflow.platform.shared.infrastructure.security.PatientDataAccessGuard;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import com.optiflow.platform.shared.interfaces.rest.CreatedResourceResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = OpenApiTags.NOTIFICATIONS)
@RestController
@RequestMapping(ApiV1.BASE_PATH)
public class DeviceTokenController {

  private final NotificationApplicationService notificationApplicationService;
  private final FromRegisterDeviceTokenRequestAssembler registerAssembler;
  private final NotificationResourceAssembler resourceAssembler;
  private final PatientDataAccessGuard patientDataAccessGuard;

  public DeviceTokenController(
      NotificationApplicationService notificationApplicationService,
      FromRegisterDeviceTokenRequestAssembler registerAssembler,
      NotificationResourceAssembler resourceAssembler,
      PatientDataAccessGuard patientDataAccessGuard) {
    this.notificationApplicationService = notificationApplicationService;
    this.registerAssembler = registerAssembler;
    this.resourceAssembler = resourceAssembler;
    this.patientDataAccessGuard = patientDataAccessGuard;
  }

  @Operation(
      summary = "Register the device of a patient",
      description = "Saves the Firebase Cloud Messaging token that receives the push notifications."
          + " A token that already exists is moved to this patient.")
  @ApiResponses(@ApiResponse(responseCode = "201", description = "Device token registered."))
  @PostMapping("/patients/{patientId}/device-tokens")
  public ResponseEntity<DeviceTokenResponse> register(
      @PathVariable UUID patientId,
      @Valid @RequestBody RegisterDeviceTokenRequest request,
      @AuthenticationPrincipal AuthenticatedUser user) {
    patientDataAccessGuard.ensureSelfPatientAccess(user, patientId);
    DeviceTokenResponse response = resourceAssembler.toResponse(
        notificationApplicationService.registerDeviceToken(
            registerAssembler.toCommand(patientId, request)));
    return CreatedResourceResponses.at("/patients/{patientId}/device-tokens", response, patientId);
  }
}
