package com.optiflow.booking.interfaces.rest;

import com.optiflow.booking.application.service.OpticalStoreApplicationService;
import com.optiflow.booking.interfaces.rest.assembler.FromRateOpticalStoreRequestAssembler;
import com.optiflow.booking.interfaces.rest.assembler.SearchBookingResponseAssembler;
import com.optiflow.booking.interfaces.rest.dto.RateOpticalStoreRequest;
import com.optiflow.booking.interfaces.rest.dto.StoreRatingResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
