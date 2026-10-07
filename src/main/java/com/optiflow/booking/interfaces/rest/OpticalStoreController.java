package com.optiflow.booking.interfaces.rest;

import com.optiflow.booking.application.query.GetOpticalStoreQuery;
import com.optiflow.booking.application.service.OpticalStoreApplicationService;
import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.interfaces.rest.assembler.FromSearchOpticalStoreRequestAssembler;
import com.optiflow.booking.interfaces.rest.assembler.SearchBookingResponseAssembler;
import com.optiflow.booking.interfaces.rest.dto.OpticalStoreListResponse;
import com.optiflow.booking.interfaces.rest.dto.OpticalStoreResponse;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
