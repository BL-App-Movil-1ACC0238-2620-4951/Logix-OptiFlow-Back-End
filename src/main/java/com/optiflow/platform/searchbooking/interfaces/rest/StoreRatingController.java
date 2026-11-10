package com.optiflow.platform.searchbooking.interfaces.rest;

import com.optiflow.platform.searchbooking.application.services.OpticalStoreApplicationService;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.FromRateOpticalStoreRequestAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.SearchBookingResponseAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.RateOpticalStoreRequest;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.StoreRatingResponse;
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

@Tag(name = OpenApiTags.RATINGS)
@RestController
@RequestMapping(ApiV1.BASE_PATH)
public class StoreRatingController {

  private final OpticalStoreApplicationService opticalStoreApplicationService;
  private final FromRateOpticalStoreRequestAssembler rateAssembler;
  private final SearchBookingResponseAssembler responseAssembler;
  private final PatientDataAccessGuard patientDataAccessGuard;

  public StoreRatingController(
      OpticalStoreApplicationService opticalStoreApplicationService,
      FromRateOpticalStoreRequestAssembler rateAssembler,
      SearchBookingResponseAssembler responseAssembler,
      PatientDataAccessGuard patientDataAccessGuard) {
    this.opticalStoreApplicationService = opticalStoreApplicationService;
    this.rateAssembler = rateAssembler;
    this.responseAssembler = responseAssembler;
    this.patientDataAccessGuard = patientDataAccessGuard;
  }

  @Operation(summary = "Rate an optical store",
      description = "Saves the rating of a patient (1 to 5) and updates the average rating of the "
          + "store. A patient can only rate in their own name.")
  @ApiResponses({
      @ApiResponse(responseCode = "201",
          description = "Rating saved with the new average of the store."),
      @ApiResponse(responseCode = "403", description = "A patient rates for another patient."),
      @ApiResponse(responseCode = "404",
          description = "The patient or the optical store was not found."),
      @ApiResponse(responseCode = "409",
          description = "The patient already rated this store or the store is not available.")
  })
  @PostMapping("/optical-stores/{id}/ratings")
  public ResponseEntity<StoreRatingResponse> rate(
      @PathVariable UUID id,
      @Valid @RequestBody RateOpticalStoreRequest request,
      @AuthenticationPrincipal AuthenticatedUser user) {
    patientDataAccessGuard.ensureSelfPatientAccess(user, request.patientId());
    StoreRatingResponse response = responseAssembler.toRatingResponse(
        opticalStoreApplicationService.rate(rateAssembler.toCommand(id, request)));
    return CreatedResourceResponses.at("/optical-stores/{id}", response, id);
  }
}
