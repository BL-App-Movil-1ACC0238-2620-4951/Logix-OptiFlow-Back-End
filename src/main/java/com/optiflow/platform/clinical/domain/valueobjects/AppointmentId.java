package com.optiflow.platform.clinical.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.util.UUID;

public record AppointmentId(UUID value) {

  public AppointmentId {
    if (value == null) {
      throw new DomainException("Appointment id is required.", 400);
    }
  }

  public static AppointmentId generate() {
    return new AppointmentId(UUID.randomUUID());
  }

  public static AppointmentId of(UUID value) {
    return new AppointmentId(value);
  }
}
