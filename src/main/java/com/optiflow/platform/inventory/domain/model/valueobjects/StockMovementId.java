package com.optiflow.platform.inventory.domain.model.valueobjects;

import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.util.UUID;

public record StockMovementId(UUID value) {

  public StockMovementId {
    if (value == null) {
      throw new DomainException("Stock movement id is required.", 400);
    }
  }

  public static StockMovementId generate() {
    return new StockMovementId(UUID.randomUUID());
  }

  public static StockMovementId of(UUID value) {
    return new StockMovementId(value);
  }
}
