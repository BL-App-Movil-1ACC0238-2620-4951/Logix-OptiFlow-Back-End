package com.optiflow.platform.inventory.application.commandhandlers;

import com.optiflow.platform.inventory.domain.exceptions.InventoryNotFoundException;
import com.optiflow.platform.inventory.domain.model.aggregates.FrameModel;
import com.optiflow.platform.inventory.domain.model.aggregates.Inventory;
import com.optiflow.platform.inventory.domain.model.commands.UpdateInventoryCommand;
import com.optiflow.platform.inventory.domain.model.entities.Stock;
import com.optiflow.platform.inventory.domain.model.entities.StockMovement;
import com.optiflow.platform.inventory.domain.model.events.InventoryWasUpdated;
import com.optiflow.platform.inventory.domain.model.events.LowStockAlertGenerated;
import com.optiflow.platform.inventory.domain.model.valueobjects.SoldProduct;
import com.optiflow.platform.inventory.domain.repositories.FrameModelRepository;
import com.optiflow.platform.inventory.domain.repositories.InventoryRepository;
import com.optiflow.platform.inventory.domain.repositories.StockMovementRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Takes the products of a closed sale out of the store's stock. Products that are not frame
 * models of the catalog, or that the store does not stock, are not controlled here and are
 * skipped.
 */
@Service
public class UpdateInventoryHandler {

  private final InventoryRepository inventoryRepository;
  private final FrameModelRepository frameModelRepository;
  private final StockMovementRepository stockMovementRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public UpdateInventoryHandler(
      InventoryRepository inventoryRepository,
      FrameModelRepository frameModelRepository,
      StockMovementRepository stockMovementRepository,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.inventoryRepository = inventoryRepository;
    this.frameModelRepository = frameModelRepository;
    this.stockMovementRepository = stockMovementRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public List<StockMovement> handle(UpdateInventoryCommand command) {
    Inventory inventory = inventoryRepository.findByOpticalStoreId(command.opticalStoreId())
        .orElseThrow(InventoryNotFoundException::new);
    Instant now = clock.instant();
    List<StockMovement> movements = new ArrayList<>();
    for (SoldProduct product : command.soldProducts()) {
      frameModelRepository.findBySku(product.sku())
          .map(FrameModel::id)
          .flatMap(frameModelId ->
              inventory.decrease(frameModelId, product.quantity(), command.saleId(), now))
          .ifPresent(movements::add);
    }
    if (movements.isEmpty()) {
      return movements;
    }
    inventoryRepository.save(inventory);
    movements.forEach(stockMovementRepository::save);
    eventPublisher.publish(new InventoryWasUpdated(inventory.id(), command.saleId(), now));
    for (StockMovement movement : movements) {
      inventory.stockOf(movement.frameModelId())
          .filter(Stock::isLow)
          .ifPresent(stock -> eventPublisher.publish(new LowStockAlertGenerated(
              inventory.id(), stock.frameModelId(), stock.quantity(), stock.minimumStock(), now)));
    }
    return movements;
  }
}
