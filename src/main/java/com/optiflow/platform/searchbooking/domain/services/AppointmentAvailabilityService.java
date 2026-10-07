package com.optiflow.platform.searchbooking.domain.services;

import com.optiflow.platform.searchbooking.domain.entities.TimeSlot;
import com.optiflow.platform.searchbooking.domain.exceptions.TimeSlotUnavailableException;
import java.time.Instant;

public class AppointmentAvailabilityService {

  public void ensureAvailable(TimeSlot timeSlot, Instant now) {
    if (!timeSlot.isAvailable(now)) {
      throw new TimeSlotUnavailableException();
    }
  }
}
