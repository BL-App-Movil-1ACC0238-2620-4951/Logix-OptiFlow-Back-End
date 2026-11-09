package com.optiflow.platform.searchbooking.interfaces.rest;

import com.optiflow.platform.searchbooking.application.services.OpticalStoreApplicationService;
import com.optiflow.platform.searchbooking.domain.model.commands.SaveFavoriteOpticalStoreCommand;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.SearchBookingResponseAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.FavoriteStoreResponse;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.SaveFavoriteStoreRequest;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import com.optiflow.platform.shared.interfaces.rest.CreatedResourceResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
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
  private final SearchBookingResponseAssembler responseAssembler;

  public FavoriteStoreController(
      OpticalStoreApplicationService opticalStoreApplicationService,
      SearchBookingResponseAssembler responseAssembler) {
    this.opticalStoreApplicationService = opticalStoreApplicationService;
    this.responseAssembler = responseAssembler;
  }

  @PostMapping("/patients/{id}/favorites")
  public ResponseEntity<FavoriteStoreResponse> save(
      @PathVariable UUID id, @Valid @RequestBody SaveFavoriteStoreRequest request) {
    FavoriteStoreResponse response = responseAssembler.toFavoriteResponse(
        opticalStoreApplicationService.saveFavorite(
            new SaveFavoriteOpticalStoreCommand(
                PatientId.of(id), OpticalStoreId.of(request.opticalStoreId()))));
    return CreatedResourceResponses.at(
        "/optical-stores/{id}", response, response.opticalStoreId());
  }
}
