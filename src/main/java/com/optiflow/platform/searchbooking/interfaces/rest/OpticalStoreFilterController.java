package com.optiflow.platform.searchbooking.interfaces.rest;

import com.optiflow.platform.searchbooking.application.services.OpticalStoreApplicationService;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.FromFilterOpticalStoreRequestAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.SearchBookingResponseAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.OpticalStoreListResponse;
import com.optiflow.platform.shared.documentation.openapi.configuration.OpenApiTags;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = OpenApiTags.OPTICAL_STORES)
@RestController
public class OpticalStoreFilterController {

  private final OpticalStoreApplicationService opticalStoreApplicationService;
  private final FromFilterOpticalStoreRequestAssembler filterAssembler;
  private final SearchBookingResponseAssembler responseAssembler;

  public OpticalStoreFilterController(
      OpticalStoreApplicationService opticalStoreApplicationService,
      FromFilterOpticalStoreRequestAssembler filterAssembler,
      SearchBookingResponseAssembler responseAssembler) {
    this.opticalStoreApplicationService = opticalStoreApplicationService;
    this.filterAssembler = filterAssembler;
    this.responseAssembler = responseAssembler;
  }

  @GetMapping("/optical-stores/search")
  public OpticalStoreListResponse search(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String address,
      @RequestParam(required = false) BigDecimal minRating) {
    return responseAssembler.toStoreList(
        opticalStoreApplicationService.filter(filterAssembler.toQuery(name, address, minRating)),
        "No optical stores match the search criteria.");
  }
}
