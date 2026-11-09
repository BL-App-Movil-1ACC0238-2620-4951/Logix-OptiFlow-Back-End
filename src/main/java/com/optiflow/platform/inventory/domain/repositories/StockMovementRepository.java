package com.optiflow.platform.inventory.domain.repositories;

import com.optiflow.platform.inventory.domain.model.entities.StockMovement;
import com.optiflow.platform.inventory.domain.model.valueobjects.FrameModelId;
import com.optiflow.platform.inventory.domain.model.valueobjects.InventoryId;
import java.time.Instant;
import java.util.List;

public interface StockMovementRepository {

  void save(StockMovement movement);

  /** Movements of an inventory from {@code from} (inclusive) to {@code to} (exclusive). */
  List<StockMovement> findByPeriod(
      InventoryId inventoryId, Instant from, Instant to, FrameModelId frameModelId);
}
