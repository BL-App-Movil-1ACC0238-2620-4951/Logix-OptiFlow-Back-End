package com.optiflow.platform.notification.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.util.UUID;

public record PatientId(UUID value) {

  public PatientId {
    if (value == null) {
      throw new DomainException("Patient id is required.", 400);
    }
  }

  public static PatientId of(UUID value) {
    return new PatientId(value);
  }
}
