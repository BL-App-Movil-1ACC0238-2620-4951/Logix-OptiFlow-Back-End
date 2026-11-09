package com.optiflow.platform.searchbooking.interfaces.rest;

import com.optiflow.platform.searchbooking.application.services.OpticalStoreApplicationService;
import com.optiflow.platform.searchbooking.domain.model.queries.FilterOpticalStoresQuery;
import com.optiflow.platform.searchbooking.domain.model.queries.GetNearbyOpticalStoresQuery;
import com.optiflow.platform.searchbooking.domain.model.queries.GetOpticalStoreQuery;
import com.optiflow.platform.searchbooking.domain.model.queries.SearchOpticalStoresQuery;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.FromOpticalStoreQueryParamsAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.SearchBookingResponseAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.OpticalStoreQueryParams;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.OpticalStoreResponse;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = OpenApiTags.OPTICAL_STORES)
@RestController
@RequestMapping(ApiV1.BASE_PATH)
public class OpticalStoreController {

  private final OpticalStoreApplicationService opticalStoreApplicationService;
  private final FromOpticalStoreQueryParamsAssembler queryAssembler;
  private final SearchBookingResponseAssembler responseAssembler;

  public OpticalStoreController(
      OpticalStoreApplicationService opticalStoreApplicationService,
      FromOpticalStoreQueryParamsAssembler queryAssembler,
      SearchBookingResponseAssembler responseAssembler) {
    this.opticalStoreApplicationService = opticalStoreApplicationService;
    this.queryAssembler = queryAssembler;
    this.responseAssembler = responseAssembler;
  }

  @Operation(summary = "List optical stores",
      description = "Without parameters, or with name or address, searches the stores; with "
          + "minRating, filters them by rating; with lat, lng and radiusKm, returns the "
          + "nearby stores ordered by distance. Public route.")
  @ApiResponses(@ApiResponse(responseCode = "200",
      description = "Optical stores that match the parameters."))
  @SecurityRequirements
  @GetMapping("/optical-stores")
  public List<OpticalStoreResponse> list(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String address,
      @RequestParam(required = false) BigDecimal minRating,
      @RequestParam(required = false) BigDecimal lat,
      @RequestParam(required = false) BigDecimal lng,
      @RequestParam(required = false) BigDecimal radiusKm) {
    OpticalStoreQueryParams params =
        new OpticalStoreQueryParams(name, address, minRating, lat, lng, radiusKm);
    return responseAssembler.toStoreResponses(switch (queryAssembler.toQuery(params)) {
      case SearchOpticalStoresQuery query -> opticalStoreApplicationService.searchStores(query);
      case FilterOpticalStoresQuery query -> opticalStoreApplicationService.findStores(query);
      case GetNearbyOpticalStoresQuery query -> opticalStoreApplicationService.findNearby(query);
    });
  }

  @Operation(summary = "Get an optical store",
      description = "Returns the store with its address, location, schedule and rating. Public "
          + "route.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Optical store found."),
      @ApiResponse(responseCode = "404", description = "The optical store was not found.")
  })
  @SecurityRequirements
  @GetMapping("/optical-stores/{id:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}")
  public OpticalStoreResponse get(@PathVariable UUID id) {
    return responseAssembler.toStoreResponse(
        opticalStoreApplicationService.get(new GetOpticalStoreQuery(OpticalStoreId.of(id))));
  }
}
