package com.optiflow.booking.domain.vo;

import com.optiflow.booking.domain.exception.DomainException;
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
