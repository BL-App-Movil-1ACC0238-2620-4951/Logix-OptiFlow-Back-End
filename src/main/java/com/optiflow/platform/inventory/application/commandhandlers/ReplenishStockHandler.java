package com.optiflow.platform.inventory.application.commandhandlers;

import com.optiflow.platform.inventory.application.results.StockMovementDetails;
import com.optiflow.platform.inventory.domain.exceptions.FrameModelNotFoundException;
import com.optiflow.platform.inventory.domain.exceptions.InventoryNotFoundException;
import com.optiflow.platform.inventory.domain.exceptions.SupplierNotFoundException;
import com.optiflow.platform.inventory.domain.model.aggregates.FrameModel;
import com.optiflow.platform.inventory.domain.model.aggregates.Inventory;
import com.optiflow.platform.inventory.domain.model.commands.ReplenishStockCommand;
import com.optiflow.platform.inventory.domain.model.entities.StockMovement;
import com.optiflow.platform.inventory.domain.model.entities.Supplier;
import com.optiflow.platform.inventory.domain.model.events.StockWasReplenished;
import com.optiflow.platform.inventory.domain.model.valueobjects.Replenishment;
import com.optiflow.platform.inventory.domain.repositories.FrameModelRepository;
import com.optiflow.platform.inventory.domain.repositories.InventoryRepository;
import com.optiflow.platform.inventory.domain.repositories.StockMovementRepository;
import com.optiflow.platform.inventory.domain.repositories.SupplierRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Adds the units delivered by a supplier and records the entry movement. */
@Service
public class ReplenishStockHandler {

  private final InventoryRepository inventoryRepository;
  private final FrameModelRepository frameModelRepository;
  private final SupplierRepository supplierRepository;
  private final StockMovementRepository stockMovementRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public ReplenishStockHandler(
      InventoryRepository inventoryRepository,
      FrameModelRepository frameModelRepository,
      SupplierRepository supplierRepository,
      StockMovementRepository stockMovementRepository,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.inventoryRepository = inventoryRepository;
    this.frameModelRepository = frameModelRepository;
    this.supplierRepository = supplierRepository;
    this.stockMovementRepository = stockMovementRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public StockMovementDetails handle(ReplenishStockCommand command) {
    Inventory inventory = inventoryRepository.findByOpticalStoreId(command.opticalStoreId())
        .orElseThrow(InventoryNotFoundException::new);
    FrameModel frameModel = frameModelRepository.findBySku(command.sku())
        .orElseThrow(FrameModelNotFoundException::new);
    Supplier supplier = supplierRepository.findById(command.supplierId())
        .orElseThrow(SupplierNotFoundException::new);
    Instant now = clock.instant();
    StockMovement movement = inventory.replenish(
        new Replenishment(frameModel.id(), supplier.id(), command.quantity()),
        command.minimumStock(),
        command.performedBy(),
        now);
    inventoryRepository.save(inventory);
    stockMovementRepository.save(movement);
    eventPublisher.publish(new StockWasReplenished(
        inventory.id(), frameModel.id(), supplier.id(), movement.quantity(), now));
    return new StockMovementDetails(movement, frameModel);
  }
}
