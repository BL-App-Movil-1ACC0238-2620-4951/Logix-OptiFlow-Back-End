package com.optiflow.platform.production.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.util.UUID;

public record LensId(UUID value) {

  public LensId {
    if (value == null) {
      throw new DomainException("Lens id is required.", 400);
    }
  }

  public static LensId generate() {
    return new LensId(UUID.randomUUID());
  }

  public static LensId of(UUID value) {
    return new LensId(value);
  }
}
