package com.optiflow.booking.domain.vo;

import com.optiflow.booking.domain.exception.DomainException;
import java.util.UUID;

public record TimeSlotId(UUID value) {

  public TimeSlotId {
    if (value == null) {
      throw new DomainException("Time slot id is required.", 400);
    }
  }

  public static TimeSlotId generate() {
    return new TimeSlotId(UUID.randomUUID());
  }

  public static TimeSlotId of(UUID value) {
    return new TimeSlotId(value);
  }
}
