package com.optiflow.platform.searchbooking.interfaces.rest;

import com.optiflow.platform.searchbooking.application.services.OpticalStoreApplicationService;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.FromRateOpticalStoreRequestAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.SearchBookingResponseAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.RateOpticalStoreRequest;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.StoreRatingResponse;
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

@Tag(name = OpenApiTags.RATINGS)
@RestController
public class StoreRatingController {

  private final OpticalStoreApplicationService opticalStoreApplicationService;
  private final FromRateOpticalStoreRequestAssembler rateAssembler;
  private final SearchBookingResponseAssembler responseAssembler;

  public StoreRatingController(
      OpticalStoreApplicationService opticalStoreApplicationService,
      FromRateOpticalStoreRequestAssembler rateAssembler,
      SearchBookingResponseAssembler responseAssembler) {
    this.opticalStoreApplicationService = opticalStoreApplicationService;
    this.rateAssembler = rateAssembler;
    this.responseAssembler = responseAssembler;
  }

  @PostMapping("/optical-stores/{id}/ratings")
  @ResponseStatus(HttpStatus.CREATED)
  public StoreRatingResponse rate(
      @PathVariable UUID id, @Valid @RequestBody RateOpticalStoreRequest request) {
    return responseAssembler.toRatingResponse(
        opticalStoreApplicationService.rate(rateAssembler.toCommand(id, request)));
  }
}
