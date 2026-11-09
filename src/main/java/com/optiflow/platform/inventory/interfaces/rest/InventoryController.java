package com.optiflow.platform.inventory.interfaces.rest;

import com.optiflow.platform.inventory.application.services.InventoryApplicationService;
import com.optiflow.platform.inventory.domain.model.queries.GetInventoryMovementsQuery;
import com.optiflow.platform.inventory.domain.model.queries.GetLowStockAlertsQuery;
import com.optiflow.platform.inventory.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.inventory.domain.model.valueobjects.Sku;
import com.optiflow.platform.inventory.interfaces.rest.assemblers.FromConsultStockRequestAssembler;
import com.optiflow.platform.inventory.interfaces.rest.assemblers.FromReplenishStockRequestAssembler;
import com.optiflow.platform.inventory.interfaces.rest.assemblers.StoreInventoryResponseAssembler;
import com.optiflow.platform.inventory.interfaces.rest.resources.ConsultStockRequest;
import com.optiflow.platform.inventory.interfaces.rest.resources.InventoryResponse;
import com.optiflow.platform.inventory.interfaces.rest.resources.LowStockAlertResponse;
import com.optiflow.platform.inventory.interfaces.rest.resources.ReplenishStockRequest;
import com.optiflow.platform.inventory.interfaces.rest.resources.StockMovementResponse;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.optiflow.platform.shared.infrastructure.security.AuthenticatedUser;
import com.optiflow.platform.shared.interfaces.rest.CreatedResourceResponses;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = OpenApiTags.INVENTORY)
@RestController
@RequestMapping(ApiV1.BASE_PATH)
public class InventoryController {

  private final InventoryApplicationService inventoryApplicationService;
  private final FromConsultStockRequestAssembler consultStockAssembler;
  private final FromReplenishStockRequestAssembler replenishAssembler;
  private final StoreInventoryResponseAssembler responseAssembler;

  public InventoryController(
      InventoryApplicationService inventoryApplicationService,
      FromConsultStockRequestAssembler consultStockAssembler,
      FromReplenishStockRequestAssembler replenishAssembler,
      StoreInventoryResponseAssembler responseAssembler) {
    this.inventoryApplicationService = inventoryApplicationService;
    this.consultStockAssembler = consultStockAssembler;
    this.replenishAssembler = replenishAssembler;
    this.responseAssembler = responseAssembler;
  }

  @GetMapping("/inventory/stock")
  public InventoryResponse stock(@ParameterObject @Valid @ModelAttribute ConsultStockRequest request) {
    return responseAssembler.toInventoryResponse(
        inventoryApplicationService.consultStock(consultStockAssembler.toQuery(request)));
  }

  @PostMapping("/inventory/replenishments")
  public ResponseEntity<StockMovementResponse> replenish(
      @Valid @RequestBody ReplenishStockRequest request,
      @AuthenticationPrincipal AuthenticatedUser user) {
    StockMovementResponse response = responseAssembler.toStockMovementResponse(
        inventoryApplicationService.replenish(
            replenishAssembler.toCommand(request, user == null ? null : user.userId())));
    return CreatedResourceResponses.at("/inventory/frames/{sku}", response, response.sku());
  }

  @GetMapping("/inventory/movements")
  public List<StockMovementResponse> movements(
      @RequestParam UUID opticalStoreId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
      @RequestParam(required = false) String sku) {
    GetInventoryMovementsQuery query = new GetInventoryMovementsQuery(
        OpticalStoreId.of(opticalStoreId), from, to,
        sku == null || sku.isBlank() ? null : Sku.of(sku));
    return inventoryApplicationService.movements(query).stream()
        .map(responseAssembler::toStockMovementResponse)
        .toList();
  }

  @GetMapping("/inventory/alerts")
  public List<LowStockAlertResponse> alerts(@RequestParam UUID opticalStoreId) {
    return inventoryApplicationService
        .lowStockAlerts(new GetLowStockAlertsQuery(OpticalStoreId.of(opticalStoreId)))
        .stream()
        .map(responseAssembler::toLowStockAlertResponse)
        .toList();
  }
}
