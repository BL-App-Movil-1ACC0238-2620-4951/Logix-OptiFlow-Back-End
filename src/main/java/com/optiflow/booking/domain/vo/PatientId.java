package com.optiflow.booking.domain.vo;

import com.optiflow.booking.domain.exception.DomainException;
import java.util.UUID;

public record PatientId(UUID value) {

  public PatientId {
    if (value == null) {
      throw new DomainException("Patient id is required.", 400);
    }
  }

  public static PatientId generate() {
    return new PatientId(UUID.randomUUID());
  }

  public static PatientId of(UUID value) {
    return new PatientId(value);
  }
}
