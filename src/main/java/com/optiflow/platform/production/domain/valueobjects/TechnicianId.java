package com.optiflow.platform.production.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.util.UUID;

public record TechnicianId(UUID value) {

  public TechnicianId {
    if (value == null) {
      throw new DomainException("Technician id is required.", 400);
    }
  }

  public static TechnicianId generate() {
    return new TechnicianId(UUID.randomUUID());
  }

  public static TechnicianId of(UUID value) {
    return new TechnicianId(value);
  }
}
