package com.optiflow.platform.inventory.domain.model.valueobjects;

import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.util.UUID;

public record SupplierId(UUID value) {

  public SupplierId {
    if (value == null) {
      throw new DomainException("Supplier id is required.", 400);
    }
  }

  public static SupplierId generate() {
    return new SupplierId(UUID.randomUUID());
  }

  public static SupplierId of(UUID value) {
    return new SupplierId(value);
  }
}
