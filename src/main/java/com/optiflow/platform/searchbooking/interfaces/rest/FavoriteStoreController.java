package com.optiflow.platform.searchbooking.interfaces.rest;

import com.optiflow.platform.searchbooking.application.services.OpticalStoreApplicationService;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.FromSaveFavoriteStoreRequestAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.SearchBookingResponseAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.FavoriteStoreResponse;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.SaveFavoriteStoreRequest;
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

@Tag(name = OpenApiTags.FAVORITES)
@RestController
@RequestMapping(ApiV1.BASE_PATH)
public class FavoriteStoreController {

  private final OpticalStoreApplicationService opticalStoreApplicationService;
  private final FromSaveFavoriteStoreRequestAssembler saveFavoriteAssembler;
  private final SearchBookingResponseAssembler responseAssembler;
  private final PatientDataAccessGuard patientDataAccessGuard;

  public FavoriteStoreController(
      OpticalStoreApplicationService opticalStoreApplicationService,
      FromSaveFavoriteStoreRequestAssembler saveFavoriteAssembler,
      SearchBookingResponseAssembler responseAssembler,
      PatientDataAccessGuard patientDataAccessGuard) {
    this.opticalStoreApplicationService = opticalStoreApplicationService;
    this.saveFavoriteAssembler = saveFavoriteAssembler;
    this.responseAssembler = responseAssembler;
    this.patientDataAccessGuard = patientDataAccessGuard;
  }

  @Operation(summary = "Save a favorite store",
      description = "Saves an optical store as a favorite of the patient. A patient can only save "
          + "their own favorites.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Favorite saved."),
      @ApiResponse(responseCode = "403",
          description = "A patient tried to save another patient's favorite."),
      @ApiResponse(responseCode = "404",
          description = "The patient or the optical store was not found."),
      @ApiResponse(responseCode = "409",
          description = "The store is already a favorite or it is not available.")
  })
  @PostMapping("/patients/{patientId}/favorites")
  public ResponseEntity<FavoriteStoreResponse> save(
      @PathVariable UUID patientId,
      @Valid @RequestBody SaveFavoriteStoreRequest request,
      @AuthenticationPrincipal AuthenticatedUser user) {
    patientDataAccessGuard.ensureSelfPatientAccess(user, patientId);
    FavoriteStoreResponse response = responseAssembler.toFavoriteResponse(
        opticalStoreApplicationService.saveFavorite(
            saveFavoriteAssembler.toCommand(patientId, request)));
    return CreatedResourceResponses.at(
        "/optical-stores/{id}", response, response.opticalStoreId());
  }
}
