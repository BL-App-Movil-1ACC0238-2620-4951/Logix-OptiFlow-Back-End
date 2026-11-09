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
import com.optiflow.platform.shared.infrastructure.security.AuthenticatedUser;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import com.optiflow.platform.shared.interfaces.rest.CreatedResourceResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

  @Operation(summary = "Consult the stock of an optical store",
      description = "Returns the quantity and minimum stock of each frame in the inventory of the "
          + "store. low is true when the quantity is at or below the minimum stock.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Stock of the store."),
      @ApiResponse(responseCode = "404", description = "The optical store has no inventory.")
  })
  @GetMapping("/inventory/stock")
  public InventoryResponse stock(@ParameterObject @Valid @ModelAttribute ConsultStockRequest request) {
    return responseAssembler.toInventoryResponse(
        inventoryApplicationService.consultStock(consultStockAssembler.toQuery(request)));
  }

  @Operation(summary = "Replenish stock",
      description = "Registers the frames delivered by a supplier as an IN movement made by the "
          + "authenticated user. The first replenishment of a frame in a store creates its stock.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Replenishment movement registered."),
      @ApiResponse(responseCode = "404", description = "The frame model, the supplier or the "
          + "inventory of the store was not found.")
  })
  @PostMapping("/inventory/replenishments")
  public ResponseEntity<StockMovementResponse> replenish(
      @Valid @RequestBody ReplenishStockRequest request,
      @AuthenticationPrincipal AuthenticatedUser user) {
    StockMovementResponse response = responseAssembler.toStockMovementResponse(
        inventoryApplicationService.replenish(
            replenishAssembler.toCommand(request, user == null ? null : user.userId())));
    return CreatedResourceResponses.at("/inventory/frames/{sku}", response, response.sku());
  }

  @Operation(summary = "List the inventory movements",
      description = "Returns the IN and OUT movements of the store from the start date to the end "
          + "date, both included, newest first. Exits of a sale carry its saleId and no "
          + "performedBy.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Movements of the period."),
      @ApiResponse(responseCode = "400",
          description = "The parameters are invalid or the start date is after the end date."),
      @ApiResponse(responseCode = "404", description = "The optical store has no inventory.")
  })
  @GetMapping("/inventory/movements")
  public List<StockMovementResponse> movements(
      @Parameter(description = "Optical store whose inventory is used.",
          example = "11111111-1111-1111-1111-111111111111")
      @RequestParam UUID opticalStoreId,
      @Parameter(description = "Start date (ISO).", example = "2026-11-01")
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @Parameter(description = "End date (ISO).", example = "2026-11-30")
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
      @Parameter(description = "Only the movements of this frame.", example = "RB-2140-BLK")
      @RequestParam(required = false) String sku) {
    GetInventoryMovementsQuery query = new GetInventoryMovementsQuery(
        OpticalStoreId.of(opticalStoreId), from, to,
        sku == null || sku.isBlank() ? null : Sku.of(sku));
    return inventoryApplicationService.movements(query).stream()
        .map(responseAssembler::toStockMovementResponse)
        .toList();
  }

  @Operation(summary = "List the low stock alerts",
      description = "Returns the frames whose quantity is at or below their minimum stock.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Current low stock alerts."),
      @ApiResponse(responseCode = "404", description = "The optical store has no inventory.")
  })
  @GetMapping("/inventory/alerts")
  public List<LowStockAlertResponse> alerts(
      @Parameter(description = "Optical store whose inventory is used.",
          example = "11111111-1111-1111-1111-111111111111")
      @RequestParam UUID opticalStoreId) {
    return inventoryApplicationService
        .lowStockAlerts(new GetLowStockAlertsQuery(OpticalStoreId.of(opticalStoreId)))
        .stream()
        .map(responseAssembler::toLowStockAlertResponse)
        .toList();
  }
}
