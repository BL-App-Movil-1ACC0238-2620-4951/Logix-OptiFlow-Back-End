package com.optiflow.platform.production.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.util.UUID;

public record LaboratoryId(UUID value) {

  public LaboratoryId {
    if (value == null) {
      throw new DomainException("Laboratory id is required.", 400);
    }
  }

  public static LaboratoryId generate() {
    return new LaboratoryId(UUID.randomUUID());
  }

  public static LaboratoryId of(UUID value) {
    return new LaboratoryId(value);
  }
}
