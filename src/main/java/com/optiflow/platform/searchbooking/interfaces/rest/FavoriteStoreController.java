package com.optiflow.platform.searchbooking.interfaces.rest;

import com.optiflow.platform.searchbooking.application.commands.SaveFavoriteOpticalStoreCommand;
import com.optiflow.platform.searchbooking.application.services.OpticalStoreApplicationService;
import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.SearchBookingResponseAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.FavoriteStoreResponse;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.SaveFavoriteStoreRequest;
import com.optiflow.platform.shared.documentation.openapi.configuration.OpenApiTags;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = OpenApiTags.FAVORITES)
@RestController
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
  @ResponseStatus(HttpStatus.CREATED)
  public FavoriteStoreResponse save(
      @PathVariable UUID id, @Valid @RequestBody SaveFavoriteStoreRequest request) {
    return responseAssembler.toFavoriteResponse(opticalStoreApplicationService.saveFavorite(
        new SaveFavoriteOpticalStoreCommand(
            PatientId.of(id), OpticalStoreId.of(request.opticalStoreId()))));
  }
}
