package com.optiflow.platform.searchbooking.interfaces.rest;

import com.optiflow.platform.searchbooking.application.queries.GetOpticalStoreQuery;
import com.optiflow.platform.searchbooking.application.services.OpticalStoreApplicationService;
import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.FromSearchOpticalStoreRequestAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.SearchBookingResponseAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.OpticalStoreListResponse;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.OpticalStoreResponse;
import com.optiflow.platform.shared.documentation.openapi.configuration.OpenApiTags;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = OpenApiTags.OPTICAL_STORES)
@RestController
public class OpticalStoreController {

  private final OpticalStoreApplicationService opticalStoreApplicationService;
  private final FromSearchOpticalStoreRequestAssembler searchAssembler;
  private final SearchBookingResponseAssembler responseAssembler;

  public OpticalStoreController(
      OpticalStoreApplicationService opticalStoreApplicationService,
      FromSearchOpticalStoreRequestAssembler searchAssembler,
      SearchBookingResponseAssembler responseAssembler) {
    this.opticalStoreApplicationService = opticalStoreApplicationService;
    this.searchAssembler = searchAssembler;
    this.responseAssembler = responseAssembler;
  }

  @GetMapping("/optical-stores")
  public OpticalStoreListResponse list(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String address) {
    return responseAssembler.toStoreList(
        opticalStoreApplicationService.search(searchAssembler.toQuery(name, address)),
        "No optical stores are available.");
  }

  @GetMapping("/optical-stores/{id:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}")
  public OpticalStoreResponse get(@PathVariable UUID id) {
    return responseAssembler.toStoreResponse(
        opticalStoreApplicationService.get(new GetOpticalStoreQuery(OpticalStoreId.of(id))));
  }
}
