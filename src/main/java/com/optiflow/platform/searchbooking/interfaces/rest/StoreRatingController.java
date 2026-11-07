package com.optiflow.platform.searchbooking.interfaces.rest;

import com.optiflow.platform.searchbooking.application.services.OpticalStoreApplicationService;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.FromRateOpticalStoreRequestAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.SearchBookingResponseAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.RateOpticalStoreRequest;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.StoreRatingResponse;
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

@Tag(name = OpenApiTags.RATINGS)
@RestController
@RequestMapping(ApiV1.BASE_PATH)
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
  public ResponseEntity<StoreRatingResponse> rate(
      @PathVariable UUID id, @Valid @RequestBody RateOpticalStoreRequest request) {
    StoreRatingResponse response = responseAssembler.toRatingResponse(
        opticalStoreApplicationService.rate(rateAssembler.toCommand(id, request)));
    return CreatedResourceResponses.at("/optical-stores/{id}", response, id);
  }
}
