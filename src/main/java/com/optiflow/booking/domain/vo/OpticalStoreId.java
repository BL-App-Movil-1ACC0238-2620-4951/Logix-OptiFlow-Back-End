package com.optiflow.booking.domain.vo;

import com.optiflow.booking.domain.exception.DomainException;
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
