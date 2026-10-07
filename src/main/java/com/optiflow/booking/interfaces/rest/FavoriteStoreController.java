package com.optiflow.booking.interfaces.rest;

import com.optiflow.booking.application.command.SaveFavoriteOpticalStoreCommand;
import com.optiflow.booking.application.service.OpticalStoreApplicationService;
import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.domain.vo.PatientId;
import com.optiflow.booking.interfaces.rest.assembler.SearchBookingResponseAssembler;
import com.optiflow.booking.interfaces.rest.dto.FavoriteStoreResponse;
import com.optiflow.booking.interfaces.rest.dto.SaveFavoriteStoreRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
