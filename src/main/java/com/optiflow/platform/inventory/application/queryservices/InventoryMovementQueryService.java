package com.optiflow.platform.inventory.application.queryservices;

import com.optiflow.platform.inventory.application.results.StockLevel;
import com.optiflow.platform.inventory.application.results.StockMovementDetails;
import com.optiflow.platform.inventory.domain.exceptions.FrameModelNotFoundException;
import com.optiflow.platform.inventory.domain.exceptions.InventoryNotFoundException;
import com.optiflow.platform.inventory.domain.model.aggregates.FrameModel;
import com.optiflow.platform.inventory.domain.model.aggregates.Inventory;
import com.optiflow.platform.inventory.domain.model.queries.GetInventoryMovementsQuery;
import com.optiflow.platform.inventory.domain.model.queries.GetLowStockAlertsQuery;
import com.optiflow.platform.inventory.domain.model.valueobjects.FrameModelId;
import com.optiflow.platform.inventory.domain.repositories.FrameModelRepository;
import com.optiflow.platform.inventory.domain.repositories.InventoryRepository;
import com.optiflow.platform.inventory.domain.repositories.StockMovementRepository;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Movement history of a store and its current low stock alerts. */
@Service
public class InventoryMovementQueryService {

  private final InventoryRepository inventoryRepository;
  private final FrameModelRepository frameModelRepository;
  private final StockMovementRepository stockMovementRepository;
  private final ConsultStockQueryService consultStockQueryService;
  private final Clock clock;

  public InventoryMovementQueryService(
      InventoryRepository inventoryRepository,
      FrameModelRepository frameModelRepository,
      StockMovementRepository stockMovementRepository,
      ConsultStockQueryService consultStockQueryService,
      Clock clock) {
    this.inventoryRepository = inventoryRepository;
    this.frameModelRepository = frameModelRepository;
    this.stockMovementRepository = stockMovementRepository;
    this.consultStockQueryService = consultStockQueryService;
    this.clock = clock;
  }

  /** Movements between both dates (inclusive), most recent first. */
  @Transactional(readOnly = true)
  public List<StockMovementDetails> handle(GetInventoryMovementsQuery query) {
    if (query.from().isAfter(query.to())) {
      throw new DomainException("The start date must not be after the end date.", 400);
    }
    Inventory inventory = inventoryRepository.findByOpticalStoreId(query.opticalStoreId())
        .orElseThrow(InventoryNotFoundException::new);
    FrameModelId frameModelId = query.sku() == null
        ? null
        : frameModelRepository.findBySku(query.sku())
            .map(FrameModel::id)
            .orElseThrow(FrameModelNotFoundException::new);
    Instant from = query.from().atStartOfDay(clock.getZone()).toInstant();
    Instant to = query.to().plusDays(1).atStartOfDay(clock.getZone()).toInstant();
    return stockMovementRepository.findByPeriod(inventory.id(), from, to, frameModelId).stream()
        .flatMap(movement -> frameModelRepository.findById(movement.frameModelId()).stream()
            .map(frameModel -> new StockMovementDetails(movement, frameModel)))
        .toList();
  }

  /** Frame models whose quantity is equal to or lower than their minimum stock. */
  @Transactional(readOnly = true)
  public List<StockLevel> handle(GetLowStockAlertsQuery query) {
    Inventory inventory = inventoryRepository.findByOpticalStoreId(query.opticalStoreId())
        .orElseThrow(InventoryNotFoundException::new);
    return consultStockQueryService.levelsOf(inventory, inventory.stocks()).stream()
        .filter(level -> level.stock().isLow())
        .toList();
  }
}
