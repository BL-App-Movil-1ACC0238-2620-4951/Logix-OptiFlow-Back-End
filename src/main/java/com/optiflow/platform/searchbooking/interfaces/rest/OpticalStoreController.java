package com.optiflow.platform.searchbooking.interfaces.rest;

import com.optiflow.platform.searchbooking.domain.model.queries.GetNearbyOpticalStoresQuery;
import com.optiflow.platform.searchbooking.domain.model.queries.GetOpticalStoreQuery;
import com.optiflow.platform.searchbooking.application.services.OpticalStoreApplicationService;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.FromOpticalStoreQueryParamsAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.SearchBookingResponseAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.OpticalStoreQueryParams;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.OpticalStoreResponse;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
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
    if (params.isNearbySearch()) {
      if (lat == null || lng == null || radiusKm == null) {
        throw new DomainException("lat, lng and radiusKm are required for a nearby search.", 400);
      }
      return responseAssembler.toStoreResponses(opticalStoreApplicationService.findNearby(
          new GetNearbyOpticalStoresQuery(lat, lng, radiusKm)));
    }
    return responseAssembler.toStoreResponses(opticalStoreApplicationService.findStores(
        queryAssembler.toQuery(params)));
  }

  @GetMapping("/optical-stores/{id:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}")
  public OpticalStoreResponse get(@PathVariable UUID id) {
    return responseAssembler.toStoreResponse(
        opticalStoreApplicationService.get(new GetOpticalStoreQuery(OpticalStoreId.of(id))));
  }
}
