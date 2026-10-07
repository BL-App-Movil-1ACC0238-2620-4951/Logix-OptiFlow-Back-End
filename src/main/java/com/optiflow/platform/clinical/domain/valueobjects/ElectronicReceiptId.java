package com.optiflow.platform.clinical.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.util.UUID;

public record ElectronicReceiptId(UUID value) {

  public ElectronicReceiptId {
    if (value == null) {
      throw new DomainException("Electronic receipt id is required.", 400);
    }
  }

  public static ElectronicReceiptId generate() {
    return new ElectronicReceiptId(UUID.randomUUID());
  }

  public static ElectronicReceiptId of(UUID value) {
    return new ElectronicReceiptId(value);
  }
}
