package com.optiflow.platform.searchbooking.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.util.UUID;

public record OpticalStoreId(UUID value) {

  public OpticalStoreId {
    if (value == null) {
      throw new DomainException("Optical store id is required.", 400);
    }
  }

  public static OpticalStoreId generate() {
    return new OpticalStoreId(UUID.randomUUID());
  }

  public static OpticalStoreId of(UUID value) {
    return new OpticalStoreId(value);
  }
}
