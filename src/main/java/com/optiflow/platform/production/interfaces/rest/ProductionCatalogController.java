package com.optiflow.platform.production.interfaces.rest;

import com.optiflow.platform.production.application.services.WorkOrderApplicationService;
import com.optiflow.platform.production.interfaces.rest.assemblers.ProductionTrackingResponseAssembler;
import com.optiflow.platform.production.interfaces.rest.resources.LaboratoryResponse;
import com.optiflow.platform.production.interfaces.rest.resources.TechnicianResponse;
import com.optiflow.platform.shared.documentation.openapi.configuration.OpenApiTags;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = OpenApiTags.TECHNICIANS_AND_LABORATORIES)
@RestController
public class ProductionCatalogController {

  private final WorkOrderApplicationService workOrderApplicationService;
  private final ProductionTrackingResponseAssembler responseAssembler;

  public ProductionCatalogController(
      WorkOrderApplicationService workOrderApplicationService,
      ProductionTrackingResponseAssembler responseAssembler) {
    this.workOrderApplicationService = workOrderApplicationService;
    this.responseAssembler = responseAssembler;
  }

  @GetMapping("/technicians")
  public List<TechnicianResponse> technicians() {
    return workOrderApplicationService.technicians().stream()
        .map(responseAssembler::toTechnicianResponse)
        .toList();
  }

  @GetMapping("/laboratories")
  public List<LaboratoryResponse> laboratories() {
    return workOrderApplicationService.laboratories().stream()
        .map(responseAssembler::toLaboratoryResponse)
        .toList();
  }
}
