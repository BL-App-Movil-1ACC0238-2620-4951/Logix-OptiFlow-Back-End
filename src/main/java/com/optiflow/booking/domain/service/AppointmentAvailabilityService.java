package com.optiflow.booking.domain.service;

import com.optiflow.booking.domain.exception.TimeSlotUnavailableException;
import com.optiflow.booking.domain.model.TimeSlot;
import java.time.Instant;

public class AppointmentAvailabilityService {

  public void ensureAvailable(TimeSlot timeSlot, Instant now) {
    if (!timeSlot.isAvailable(now)) {
      throw new TimeSlotUnavailableException();
    }
  }
}
