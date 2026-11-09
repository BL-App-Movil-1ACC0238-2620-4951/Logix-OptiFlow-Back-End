package com.optiflow.platform.notification.interfaces.rest;

import com.optiflow.platform.notification.application.services.LoyaltyApplicationService;
import com.optiflow.platform.notification.domain.model.queries.GetLoyaltyAccountByPatientQuery;
import com.optiflow.platform.notification.interfaces.rest.assemblers.LoyaltyAccountResourceAssembler;
import com.optiflow.platform.notification.interfaces.rest.resources.LoyaltyAccountResponse;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.infrastructure.security.AuthenticatedUser;
import com.optiflow.platform.shared.infrastructure.security.PatientDataAccessGuard;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = OpenApiTags.LOYALTY_ACCOUNTS)
@RestController
@RequestMapping(ApiV1.BASE_PATH)
public class LoyaltyAccountController {

  private final LoyaltyApplicationService loyaltyApplicationService;
  private final LoyaltyAccountResourceAssembler resourceAssembler;
  private final PatientDataAccessGuard patientDataAccessGuard;

  public LoyaltyAccountController(
      LoyaltyApplicationService loyaltyApplicationService,
      LoyaltyAccountResourceAssembler resourceAssembler,
      PatientDataAccessGuard patientDataAccessGuard) {
    this.loyaltyApplicationService = loyaltyApplicationService;
    this.resourceAssembler = resourceAssembler;
    this.patientDataAccessGuard = patientDataAccessGuard;
  }

  @Operation(
      summary = "Get the loyalty account of a patient",
      description = "The account is opened with the first birthday discount.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Loyalty account of the patient."),
      @ApiResponse(responseCode = "404", description = "The patient has no loyalty account yet.")
  })
  @GetMapping("/patients/{patientId}/loyalty-account")
  public LoyaltyAccountResponse get(
      @PathVariable UUID patientId, @AuthenticationPrincipal AuthenticatedUser user) {
    patientDataAccessGuard.ensureSelfPatientAccess(user, patientId);
    return resourceAssembler.toResponse(
        loyaltyApplicationService.getByPatient(new GetLoyaltyAccountByPatientQuery(patientId)));
  }
}
