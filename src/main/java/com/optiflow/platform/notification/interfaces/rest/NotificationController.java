package com.optiflow.platform.notification.interfaces.rest;

import com.optiflow.platform.notification.application.services.NotificationApplicationService;
import com.optiflow.platform.notification.domain.model.aggregates.Notification;
import com.optiflow.platform.notification.domain.model.queries.GetNotificationByIdQuery;
import com.optiflow.platform.notification.domain.model.queries.GetNotificationsByPatientQuery;
import com.optiflow.platform.notification.interfaces.rest.assemblers.FromUpdateNotificationRequestAssembler;
import com.optiflow.platform.notification.interfaces.rest.assemblers.NotificationResourceAssembler;
import com.optiflow.platform.notification.interfaces.rest.resources.NotificationResponse;
import com.optiflow.platform.notification.interfaces.rest.resources.UpdateNotificationRequest;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.infrastructure.security.AuthenticatedUser;
import com.optiflow.platform.shared.infrastructure.security.PatientDataAccessGuard;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = OpenApiTags.NOTIFICATIONS)
@RestController
@RequestMapping(ApiV1.BASE_PATH)
public class NotificationController {

  private final NotificationApplicationService notificationApplicationService;
  private final FromUpdateNotificationRequestAssembler updateAssembler;
  private final NotificationResourceAssembler resourceAssembler;
  private final PatientDataAccessGuard patientDataAccessGuard;

  public NotificationController(
      NotificationApplicationService notificationApplicationService,
      FromUpdateNotificationRequestAssembler updateAssembler,
      NotificationResourceAssembler resourceAssembler,
      PatientDataAccessGuard patientDataAccessGuard) {
    this.notificationApplicationService = notificationApplicationService;
    this.updateAssembler = updateAssembler;
    this.resourceAssembler = resourceAssembler;
    this.patientDataAccessGuard = patientDataAccessGuard;
  }

  @Operation(
      summary = "List the notifications of a patient",
      description = "Returns the notifications of the patient, newest first. A reminder is"
          + " listed once its scheduled date arrives.")
  @ApiResponses(@ApiResponse(responseCode = "200", description = "Notifications of the patient."))
  @GetMapping("/patients/{patientId}/notifications")
  public List<NotificationResponse> findByPatient(
      @PathVariable UUID patientId, @AuthenticationPrincipal AuthenticatedUser user) {
    patientDataAccessGuard.ensureSelfPatientAccess(user, patientId);
    return notificationApplicationService
        .findByPatient(new GetNotificationsByPatientQuery(patientId)).stream()
        .map(resourceAssembler::toResponse)
        .toList();
  }

  @Operation(summary = "Get a notification")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Notification found."),
      @ApiResponse(responseCode = "404", description = "The notification was not found.")
  })
  @GetMapping("/notifications/{id}")
  public NotificationResponse get(
      @PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser user) {
    return resourceAssembler.toResponse(ownNotification(id, user));
  }

  @Operation(
      summary = "Mark a notification as read",
      description = "Accepts {\"read\": true}; a notification that was already read returns 409.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Notification marked as read."),
      @ApiResponse(responseCode = "404", description = "The notification was not found."),
      @ApiResponse(responseCode = "409", description = "The notification was already read.")
  })
  @PatchMapping("/notifications/{id}")
  public NotificationResponse update(
      @PathVariable UUID id,
      @Valid @RequestBody UpdateNotificationRequest request,
      @AuthenticationPrincipal AuthenticatedUser user) {
    ownNotification(id, user);
    return resourceAssembler.toResponse(
        notificationApplicationService.markAsRead(updateAssembler.toCommand(id, request)));
  }

  private Notification ownNotification(UUID id, AuthenticatedUser user) {
    Notification notification =
        notificationApplicationService.getById(new GetNotificationByIdQuery(id));
    patientDataAccessGuard.ensureSelfPatientAccess(user, notification.patientId().value());
    return notification;
  }
}
