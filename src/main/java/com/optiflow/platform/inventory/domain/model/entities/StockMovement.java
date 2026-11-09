package com.optiflow.platform.inventory.domain.model.entities;

import com.optiflow.platform.inventory.domain.model.valueobjects.FrameModelId;
import com.optiflow.platform.inventory.domain.model.valueobjects.InventoryId;
import com.optiflow.platform.inventory.domain.model.valueobjects.MovementReason;
import com.optiflow.platform.inventory.domain.model.valueobjects.MovementType;
import com.optiflow.platform.inventory.domain.model.valueobjects.StockMovementId;
import com.optiflow.platform.inventory.domain.model.valueobjects.SupplierId;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.time.Instant;
import java.util.UUID;

/**
 * Entry or exit of a frame model in an inventory; together they form the movement history.
 *
 * <p>{@code performedBy} is empty for the exits of a sale, which come from an event and not
 * from a user.
 */
public record StockMovement(
    StockMovementId id,
    InventoryId inventoryId,
    FrameModelId frameModelId,
    MovementType type,
    MovementReason reason,
    int quantity,
    UUID saleId,
    SupplierId supplierId,
    UUID performedBy,
    Instant createdAt) {

  public StockMovement {
    if (id == null || inventoryId == null || frameModelId == null || type == null
        || reason == null || createdAt == null) {
      throw new DomainException("Stock movement data is incomplete.", 400);
    }
    if (quantity <= 0) {
      throw new DomainException("Moved quantity must be greater than zero.", 400);
    }
  }
}
