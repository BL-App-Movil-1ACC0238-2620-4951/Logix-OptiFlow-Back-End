package com.optiflow.platform.inventory.domain.model.valueobjects;

import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.util.UUID;

public record InventoryId(UUID value) {

  public InventoryId {
    if (value == null) {
      throw new DomainException("Inventory id is required.", 400);
    }
  }

  public static InventoryId generate() {
    return new InventoryId(UUID.randomUUID());
  }

  public static InventoryId of(UUID value) {
    return new InventoryId(value);
  }
}
