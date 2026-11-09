package com.optiflow.platform.inventory.application.services;

import com.optiflow.platform.inventory.application.commandhandlers.ReplenishStockHandler;
import com.optiflow.platform.inventory.application.queryservices.ConsultStockQueryService;
import com.optiflow.platform.inventory.application.queryservices.InventoryMovementQueryService;
import com.optiflow.platform.inventory.application.results.FrameModelDetails;
import com.optiflow.platform.inventory.application.results.InventoryStock;
import com.optiflow.platform.inventory.application.results.StockLevel;
import com.optiflow.platform.inventory.application.results.StockMovementDetails;
import com.optiflow.platform.inventory.domain.model.commands.ReplenishStockCommand;
import com.optiflow.platform.inventory.domain.model.queries.ConsultStockQuery;
import com.optiflow.platform.inventory.domain.model.queries.GetFrameModelBySkuQuery;
import com.optiflow.platform.inventory.domain.model.queries.GetInventoryMovementsQuery;
import com.optiflow.platform.inventory.domain.model.queries.GetLowStockAlertsQuery;
import java.util.List;
import org.springframework.stereotype.Service;

/** Facade of the stock, replenishment, movement and alert use cases. */
@Service
public class InventoryApplicationService {

  private final ReplenishStockHandler replenishStockHandler;
  private final ConsultStockQueryService consultStockQueryService;
  private final InventoryMovementQueryService inventoryMovementQueryService;

  public InventoryApplicationService(
      ReplenishStockHandler replenishStockHandler,
      ConsultStockQueryService consultStockQueryService,
      InventoryMovementQueryService inventoryMovementQueryService) {
    this.replenishStockHandler = replenishStockHandler;
    this.consultStockQueryService = consultStockQueryService;
    this.inventoryMovementQueryService = inventoryMovementQueryService;
  }

  public StockMovementDetails replenish(ReplenishStockCommand command) {
    return replenishStockHandler.handle(command);
  }

  public InventoryStock consultStock(ConsultStockQuery query) {
    return consultStockQueryService.handle(query);
  }

  public FrameModelDetails getFrameModel(GetFrameModelBySkuQuery query) {
    return consultStockQueryService.handle(query);
  }

  public List<StockMovementDetails> movements(GetInventoryMovementsQuery query) {
    return inventoryMovementQueryService.handle(query);
  }

  public List<StockLevel> lowStockAlerts(GetLowStockAlertsQuery query) {
    return inventoryMovementQueryService.handle(query);
  }
}
