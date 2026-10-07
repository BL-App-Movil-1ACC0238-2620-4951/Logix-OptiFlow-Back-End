package com.optiflow.platform.production.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.util.UUID;

public record SaleId(UUID value) {

  public SaleId {
    if (value == null) {
      throw new DomainException("Sale id is required.", 400);
    }
  }

  public static SaleId generate() {
    return new SaleId(UUID.randomUUID());
  }

  public static SaleId of(UUID value) {
    return new SaleId(value);
  }
}
