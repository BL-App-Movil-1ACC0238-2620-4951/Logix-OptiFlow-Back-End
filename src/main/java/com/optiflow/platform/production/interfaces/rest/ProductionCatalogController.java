package com.optiflow.platform.production.interfaces.rest;

import com.optiflow.platform.production.application.services.WorkOrderApplicationService;
import com.optiflow.platform.production.domain.model.queries.GetLaboratoriesQuery;
import com.optiflow.platform.production.domain.model.queries.GetTechniciansQuery;
import com.optiflow.platform.production.interfaces.rest.assemblers.ProductionTrackingResponseAssembler;
import com.optiflow.platform.production.interfaces.rest.resources.LaboratoryResponse;
import com.optiflow.platform.production.interfaces.rest.resources.TechnicianResponse;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = OpenApiTags.TECHNICIANS_AND_LABORATORIES)
@RestController
@RequestMapping(ApiV1.BASE_PATH)
public class ProductionCatalogController {

  private final WorkOrderApplicationService workOrderApplicationService;
  private final ProductionTrackingResponseAssembler responseAssembler;

  public ProductionCatalogController(
      WorkOrderApplicationService workOrderApplicationService,
      ProductionTrackingResponseAssembler responseAssembler) {
    this.workOrderApplicationService = workOrderApplicationService;
    this.responseAssembler = responseAssembler;
  }

  @Operation(summary = "List technicians",
      description = "Returns the technicians that can be assigned to an order.")
  @ApiResponses(@ApiResponse(responseCode = "200", description = "Technicians."))
  @GetMapping("/technicians")
  public List<TechnicianResponse> technicians() {
    return workOrderApplicationService.technicians(new GetTechniciansQuery()).stream()
        .map(responseAssembler::toTechnicianResponse)
        .toList();
  }

  @Operation(summary = "List laboratories",
      description = "Returns the laboratories that can receive an order.")
  @ApiResponses(@ApiResponse(responseCode = "200", description = "Laboratories."))
  @GetMapping("/laboratories")
  public List<LaboratoryResponse> laboratories() {
    return workOrderApplicationService.laboratories(new GetLaboratoriesQuery()).stream()
        .map(responseAssembler::toLaboratoryResponse)
        .toList();
  }
}
