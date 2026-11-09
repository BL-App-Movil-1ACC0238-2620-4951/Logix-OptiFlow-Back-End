package com.optiflow.platform.inventory.domain.model.valueobjects;

import com.optiflow.platform.shared.domain.exceptions.DomainException;

/** Units of a frame model that a supplier delivers to an inventory. */
public record Replenishment(FrameModelId frameModelId, SupplierId supplierId, int quantity) {

  public static final int MAX_QUANTITY = 1000;

  public Replenishment {
    if (frameModelId == null || supplierId == null) {
      throw new DomainException("Frame model and supplier are required.", 400);
    }
    if (quantity <= 0 || quantity > MAX_QUANTITY) {
      throw new DomainException(
          "Replenished quantity must be between 1 and " + MAX_QUANTITY + ".", 400);
    }
  }
}
